package com.newpick4u.news.news.application.usecase;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.newpick4u.common.resolver.dto.CurrentUserInfoDto;
import com.newpick4u.common.resolver.dto.UserRole;
import com.newpick4u.news.news.application.dto.NewsInfoDto;
import com.newpick4u.news.news.application.dto.NewsTagDto;
import com.newpick4u.news.news.application.dto.response.NewsResponseDto;
import com.newpick4u.news.news.application.dto.response.NewsSummaryDto;
import com.newpick4u.news.news.application.dto.response.PageResponse;
import com.newpick4u.news.news.application.dto.NewsSearchCriteria;
import com.newpick4u.news.news.domain.entity.News;
import com.newpick4u.news.news.domain.entity.NewsTag;
import com.newpick4u.news.news.domain.entity.TagInbox;
import com.newpick4u.news.news.domain.model.Pagination;
import com.newpick4u.news.news.domain.repository.NewsRepository;
import com.newpick4u.news.news.domain.repository.TagInboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class NewsServiceImpl implements NewsService {
    private final NewsRepository newsRepository;
    private final TagInboxRepository tagInboxRepository;
    private final ObjectMapper objectMapper;
    private final RecommendationCacheOperator recommendationCacheOperator;
    private final ViewCountCacheOperator viewCountCacheOperator;
    private final TagIndexQueueOperator tagIndexQueueOperator;
    private final NewsVectorQueueOperator newsVectorQueueOperator;

    @Override
    @Transactional
      public void saveNewsInfo(NewsInfoDto dto) {
        simulateFailures(dto.aiNewsId()); // 테스트 조건 시뮬레이션

        if (newsRepository.existsByAiNewsId(dto.aiNewsId())) {
              throw new IllegalStateException("이미 저장된 뉴스입니다: " + dto.aiNewsId());
          }
          News news = News.create(dto.aiNewsId(), dto.title(), dto.content(), dto.url(), dto.publishedDate(), 0L);
          newsRepository.save(news);
      }
    @Override
    @Transactional
    public void updateNewsTagList(NewsTagDto dto) {
        simulateFailures(dto.aiNewsId()); // 테스트 조건 시뮬레이션

        validateTagListSize(dto);
        newsRepository.findByAiNewsId(dto.aiNewsId())
                .ifPresentOrElse(
                        news -> {
                            applyTagList(news, dto);
                            // 벡터 생성 요청을 대기열에 등록
                            newsVectorQueueOperator.enqueueNewsVector(news.getId());
                        },
                        () -> saveInbox(dto)
                );
    }

    // 내부 메서드
    private void validateTagListSize(NewsTagDto dto) {
        if (dto.tagList() == null || dto.tagList().size() > 10) {
            throw new IllegalArgumentException("뉴스 태그는 최대 10개까지 존재합니다.");
        }
    }

    private void applyTagList(News news, NewsTagDto dto) {
        List<NewsTag> tags = dto.tagList().stream()
                .map(tag -> NewsTag.create(tag.id(), tag.name(), news))
                .toList();
        news.addTags(tags);

        // Redis 대기열(Set)에 태그 추가
        dto.tagList().forEach(tag -> tagIndexQueueOperator.enqueuePendingTag(tag.name()));
    }

    private void saveInbox(NewsTagDto dto) {
        String json = serializeToJson(dto);
        TagInbox inbox = TagInbox.create(dto.aiNewsId(), json);
        tagInboxRepository.save(inbox);
    }

    private String serializeToJson(NewsTagDto dto) {
        try {
            return objectMapper.writeValueAsString(dto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("태그 인박스 직렬화 실패", e);
        }
    }

    // 테스트용 시뮬레이션
    private static final Map<String, Integer> failureMap = new ConcurrentHashMap<>();

    private static void simulateFailures(String aiNewsId) {
        if ("fail-once".equals(aiNewsId)) {
            int count = failureMap.getOrDefault(aiNewsId, 0);
            log.info("[SimulateFail] 실행 카운트 - aiNewsId: {}, count: {}", aiNewsId, count);

            if (count < 1) {
                failureMap.put(aiNewsId, count + 1); // 첫 실패 기록
                log.warn("[SimulateFail] 첫 번째 실패 유도: {}", aiNewsId);

                throw new RuntimeException("첫 번째 실패 유도");
            }
        }
        if ("fail-me".equals(aiNewsId)) {
            throw new RuntimeException("무조건 실패 유도");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public NewsResponseDto getNews(UUID id, CurrentUserInfoDto userInfoDto) {
        boolean isMaster = userInfoDto.role() == UserRole.ROLE_MASTER;
        News news = newsRepository.findNewsByRole(id, isMaster)
                .orElseThrow(() -> new IllegalArgumentException("뉴스를 찾을 수 없습니다."));

        List<String> tags = news.getNewsTagList().stream()
                .map(NewsTag::getName)
                .toList();

        recommendationCacheOperator.incrementUserTagScore(userInfoDto.userId(), tags);

        if (viewCountCacheOperator.canIncreaseView(id, userInfoDto.userId())) {
            viewCountCacheOperator.incrementViewCount(id);
        }
        news.setView(viewCountCacheOperator.getViewCount(news.getId()));

        return NewsResponseDto.from(news);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<NewsSummaryDto> searchNewsList(NewsSearchCriteria request, CurrentUserInfoDto userInfoDto) {
        boolean isMaster = userInfoDto.role() == UserRole.ROLE_MASTER;
        Pagination<News> pagination = newsRepository.searchNewsList(request, isMaster);
        return PageResponse.from(pagination).map(NewsSummaryDto::from);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NewsSummaryDto> recommendTop10(CurrentUserInfoDto userInfo) {
        Long userId = userInfo.userId();

        // 1. Redis에서 추천된 뉴스 ID 가져옴
        List<String> cachedNewsIds = recommendationCacheOperator.getRecommendedNews(userId);
        if (cachedNewsIds != null && !cachedNewsIds.isEmpty()) {
            List<UUID> ids = cachedNewsIds.stream().map(UUID::fromString).toList();
            // 2. DB에서 추천 뉴스 조회
            List<News> newsList = newsRepository.findByIds(ids);
            return newsList.stream().map(NewsSummaryDto::from).toList();
        }

        // 3. 추천 뉴스 캐시 없으면 최신 뉴스 fallback
        List<News> fallbackNews = newsRepository.findLatestNews(10);
        return fallbackNews.stream()
                .map(NewsSummaryDto::from)
                .toList();
    }
}

