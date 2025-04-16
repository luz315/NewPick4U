package com.newpick4u.news;

import static org.assertj.core.api.Assertions.within;
import com.newpick4u.common.resolver.dto.CurrentUserInfoDto;
import com.newpick4u.common.resolver.dto.UserRole;
import com.newpick4u.news.news.application.dto.response.NewsSummaryDto;
import com.newpick4u.news.news.application.usecase.NewsService;
import com.newpick4u.news.news.domain.entity.News;
import com.newpick4u.news.news.domain.entity.NewsTag;
import com.newpick4u.news.news.domain.repository.NewsRepository;
import com.newpick4u.news.news.domain.repository.NewsRepositoryCustom;
import com.newpick4u.news.news.infrastructure.redis.RedisConfig;
import com.newpick4u.news.news.infrastructure.redis.RedissonConfig;
import com.newpick4u.news.news.infrastructure.redis.UserTagRedisOperator;
import com.newpick4u.news.news.infrastructure.util.NewsRecommender;
import com.newpick4u.news.news.infrastructure.util.VectorSimilarityCalculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.IntStream;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "spring.data.redis.host=localhost",
        "spring.data.redis.port=6379",
        "spring.data.redis.password=systempass"
})
//
//@TestPropertySource(properties = "spring.config.import=")
//@ImportAutoConfiguration(exclude = {RedisConfig.class, RedissonConfig.class })
class NewsRecommendationTest {

    @Autowired
    NewsRepository newsRepository;

    @Autowired
    UserTagRedisOperator userTagRedisOperator;

    @Autowired
    NewsService newsService;

    @Autowired
    NewsRepositoryCustom newsRepositoryCustom;

    @Autowired
    NewsRecommender newsRecommender;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @BeforeEach
    void setUp() {
        newsRepository.deleteAll(); // 깨끗한 상태 유지
        redisTemplate.getConnectionFactory().getConnection().flushDb(); // Redis 초기화
    }

    @Test
    void 전체_흐름_및_추천결과_검증() {
        // 1. 뉴스 50개 생성 및 저장
        List<String> tagsPool = IntStream.range(0, 30).mapToObj(i -> "tag" + i).toList();
        List<News> newsList = IntStream.range(0, 50)
            .mapToObj(i -> {
                News news = News.create("ai-news-" + i, "title" + i, "content", "url", "2023", 0L);
                List<NewsTag> tagList = IntStream.range(0, 3)
                    .mapToObj(j -> NewsTag.create(UUID.randomUUID(), tagsPool.get((i + j) % tagsPool.size()), news))
                    .toList();
                news.addTags(tagList);
                return news;
            }).toList();
        newsRepository.saveAll(newsList);

        // 2. 관심 태그 기록
        Long userId = 123L;
        List<String> interestedTags = tagsPool.subList(0, 20);
        userTagRedisOperator.incrementUserTags(userId, interestedTags);

        // 3. 추천 호출
        CurrentUserInfoDto user = new CurrentUserInfoDto(userId, UserRole.ROLE_USER);
        List<NewsSummaryDto> recommended = newsService.recommendTop10(user);

        // 4. 추천 결과 10개인지 검증
        assertThat(recommended).hasSize(10);

        // ✅ 5. 유사도가 높은 뉴스가 먼저 오는지 (간접검증: 태그 포함 개수 체크)
        List<String> userTags = new ArrayList<>(interestedTags);
        for (NewsSummaryDto dto : recommended) {
            News news = newsRepositoryCustom.findWithTagsById(dto.id())
                    .orElseThrow();

            long matchedTags = news.getNewsTagList().stream()
                .map(NewsTag::getName)
                .filter(userTags::contains)
                .count();
            assertThat(matchedTags).isGreaterThan(0);
        }
    }

    @Test
    void 캐시_적용_결과_검증() {
        Long userId = 456L;
        List<String> tagList = List.of("tag1", "tag2", "tag3");
        userTagRedisOperator.incrementUserTags(userId, tagList);

        // 실제 뉴스 10개 저장
        List<News> newsList = IntStream.range(0, 10)
                .mapToObj(i -> {
                    News news = News.create("ai-news-cached-" + i, "title" + i, "content", "url", "2023", 0L);
                    List<NewsTag> tags = tagList.stream()
                            .map(tag -> NewsTag.create(UUID.randomUUID(), tag, news))
                            .toList();
                    news.addTags(tags);
                    return news;
                })
                .toList();
        newsRepository.saveAll(newsList);

        // 캐시 저장 (실제 저장된 UUID 기반)
        List<String> savedNewsIds = newsList.stream()
                .map(n -> n.getId().toString())
                .toList();
        userTagRedisOperator.cacheRecommendedNews(userId, savedNewsIds);

        // 2. recommendTop10 호출 시 캐시 사용되는지 검증
        CurrentUserInfoDto user = new CurrentUserInfoDto(userId, UserRole.ROLE_USER);
        List<NewsSummaryDto> recommended = newsService.recommendTop10(user);

        assertThat(recommended).hasSize(10);
        assertThat(recommended).allMatch(dto -> savedNewsIds.contains(dto.id().toString()));
    }

    @Test
    void 태그_50개_초과시_정상_제한되는지() {
        Long userId = 789L;
        List<String> manyTags = IntStream.range(0, 100).mapToObj(i -> "tag" + i).toList();

        userTagRedisOperator.incrementUserTags(userId, manyTags);

        Map<String, Double> tagScoreMap = userTagRedisOperator.getUserTagScoreMap(userId);

        assertThat(tagScoreMap).hasSizeLessThanOrEqualTo(50); // ✅ 최대 50개 제한 확인
    }

    @Test
    void 코사인_유사도_정확성_테스트() {
        double[] vec1 = {1, 0, 1};
        double[] vec2 = {1, 1, 1};
        double[] vec3 = {0, 1, 0};

        double sim12 = VectorSimilarityCalculator.cosineSimilarity(vec1, vec2);
        double sim13 = VectorSimilarityCalculator.cosineSimilarity(vec1, vec3);

        assertThat(sim12).isGreaterThan(sim13); // ✅ vec1과 vec2가 더 유사함
        assertThat(sim12).isCloseTo(0.816, within(0.01));
    }
}
