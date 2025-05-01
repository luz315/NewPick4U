package com.newpick4u.news.news.application.usecase;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.newpick4u.common.exception.CustomException;
import com.newpick4u.common.resolver.dto.CurrentUserInfoDto;
import com.newpick4u.common.resolver.dto.UserRole;
import com.newpick4u.news.global.exception.NewsErrorCode;
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

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class NewsServiceImpl implements NewsService {

//    private final MeterRegistry meterRegistry;
//    private final Counter updateNewsTagSuccessCounter;
//    private final Counter updateNewsTagFailCounter;
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
            throw CustomException.from(NewsErrorCode.DUPLICATE_NEWS);
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

    @Override
    @Transactional
    public void saveNewsInfoAndUpdateTags(NewsInfoDto newsInfoDto) {
        saveNewsInfo(newsInfoDto); // 기존 뉴스 저장
        tagInboxRepository.findByAiNewsId(newsInfoDto.aiNewsId()).ifPresent(inbox -> {
            try {
                updateNewsTagList(objectMapper.readValue(inbox.getJsonPayload(), NewsTagDto.class));
                tagInboxRepository.delete(inbox);
                log.info("[TagInbox] 태그 적용 완료 및 삭제: aiNewsId={}", newsInfoDto.aiNewsId());
            } catch (Exception e) {
                log.warn("[TagInbox] 처리 실패: aiNewsId={}", newsInfoDto.aiNewsId(), e);
                throw CustomException.from(NewsErrorCode.TAG_INBOX_SERIALIZATION_FAIL);
            }
        });
    }

    // 내부 메서드
    private void validateTagListSize(NewsTagDto dto) {
        if (dto.tagList() == null || dto.tagList().size() > 10) {
            throw CustomException.from(NewsErrorCode.TAG_LIMIT_EXCEEDED);
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
            throw CustomException.from(NewsErrorCode.TAG_INBOX_SERIALIZATION_FAIL);
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

                throw CustomException.from(NewsErrorCode.TEST_SIMULATED_FAILURE_ONCE);
            }
        }
        if ("fail-me".equals(aiNewsId)) {
            throw CustomException.from(NewsErrorCode.TEST_SIMULATED_FAILURE_ALWAYS);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public NewsResponseDto getNews(UUID id, CurrentUserInfoDto userInfoDto) {
        long start = System.currentTimeMillis();
        News news = getNewsByRole(id, userInfoDto.role());

        updateUserTagScore(userInfoDto.userId(), news);
        updateViewCount(id, userInfoDto.userId(), news);

        long total = System.currentTimeMillis() - start;
        log.info("[Timer] getNews 전체 수행시간 = {}ms", total);

        return NewsResponseDto.from(news);
    }

    private News getNewsByRole(UUID id, UserRole role) {
        boolean isMaster = role == UserRole.ROLE_MASTER;
        return newsRepository.findNewsByRole(id, isMaster)
                .orElseThrow(() -> CustomException.from(NewsErrorCode.NEWS_NOT_FOUND));
    }

    private void updateUserTagScore(Long userId, News news) {
        List<String> tags = news.getNewsTagList().stream()
                .map(NewsTag::getName)
                .toList();

        long start = System.currentTimeMillis();
        recommendationCacheOperator.incrementUserTagScore(userId, tags);
        log.info("[Timer] 사용자 태그 점수 증가 끝, 소요시간 = {}ms", System.currentTimeMillis() - start);
    }

    private void updateViewCount(UUID newsId, Long userId, News news) {
        long start = System.currentTimeMillis();
        if (viewCountCacheOperator.isViewToday(newsId, userId)) {
            viewCountCacheOperator.incrementViewCount(newsId);
        }
        long viewCount = viewCountCacheOperator.getViewCount(newsId);
        news.updateView(viewCount);

        viewCountCacheOperator.updatePopularityScore(newsId, viewCount, news.getCreatedAt());
        log.info("[Timer] 조회수 처리 끝, 소요시간 = {}ms", System.currentTimeMillis() - start);
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
        long start = System.currentTimeMillis();

        // 1. Redis에서 추천된 뉴스 ID 가져옴
        List<NewsSummaryDto> result = getRecommendedNewsFromCache(userId);

        // 2. 추천 뉴스 캐시 없으면 최신 뉴스 fallback
        if (result.isEmpty()) {
            result = getFallbackNews();
        }

        long total = System.currentTimeMillis() - start;
        if (total >= 2500) { // P95 슬로우 기준: 2.5초 이상
            log.warn("[SLOW][recommendTop10] 전체 수행시간 = {}ms, userId={}", total, userId);
        } else {
            log.info("[Timer] recommendTop10 전체 수행시간 = {}ms", total);
        }
        return result;
    }

    private List<NewsSummaryDto> getRecommendedNewsFromCache(Long userId) {
        long start = System.currentTimeMillis();
        List<String> cachedNewsIds = recommendationCacheOperator.getRecommendedNews(userId);
        long duration = System.currentTimeMillis() - start;

        log.info("[Timer] 추천 캐시 조회 끝, 소요시간 = {}ms", duration);

        if (cachedNewsIds == null || cachedNewsIds.isEmpty()) return List.of();

        List<UUID> newsUUIDList = cachedNewsIds.stream().map(UUID::fromString).toList();
        List<News> newsList = newsRepository.findByIds(newsUUIDList);

        log.info("[Timer] 추천 뉴스 DB 조회 끝, 소요시간 = {}ms", System.currentTimeMillis() - start);
        return newsList.stream().map(NewsSummaryDto::from).toList();
    }

    private List<NewsSummaryDto> getFallbackNews() {
        List<UUID> fallbackNewsIds = recommendationCacheOperator.getFallbackLatestNews();
        List<News> fallbackNewsList;

        if (fallbackNewsIds != null && !fallbackNewsIds.isEmpty()) {
            // 캐시된 fallback ID로 조회
            fallbackNewsList = newsRepository.findByIds(fallbackNewsIds);
        } else {
            // fallback 캐시가 없으면 DB 조회 후 캐시 등록
            fallbackNewsList = newsRepository.findLatestNews(10);
            List<UUID> ids = fallbackNewsList.stream().map(News::getId).toList();
            recommendationCacheOperator.cacheFallbackLatestNews(ids);
        }

        return fallbackNewsList.stream().map(NewsSummaryDto::from).toList();
    }

    @Override
    public List<NewsSummaryDto> getPopularTop10() {
        Set<String> newsIdStrs = viewCountCacheOperator.getTopPopularNewsIds(10);
        if (newsIdStrs == null || newsIdStrs.isEmpty()) return List.of();

        List<UUID> ids = newsIdStrs.stream().map(UUID::fromString).toList();
        List<News> newsList = newsRepository.findByIds(ids);
        return newsList.stream().map(NewsSummaryDto::from).toList();
    }

    @Override
    @Transactional
    public void deleteNews(UUID newsId, CurrentUserInfoDto userInfoDto) {
        News news = getNewsByRole(newsId, userInfoDto.role());

        news.markAsDeleted(LocalDateTime.now(), userInfoDto.userId());
        newsRepository.save(news);

        // 연관된 Redis 캐시 삭제
        removeRedisCache(newsId);

        log.info("[뉴스 삭제] newsId={}, deletedBy={}", newsId, userInfoDto.userId());
    }

    private void removeRedisCache(UUID newsId) {
        // 인기 점수 초기화
        viewCountCacheOperator.updatePopularityScore(newsId, 0L, LocalDateTime.now());
        // 추천 목록에서 제거
        recommendationCacheOperator.removeFromAllRecommendations(newsId);
        // 벡터 대기열에서 제거
        newsVectorQueueOperator.removeFromQueue(newsId);
    }

    // 내부 API용 단순 조회 메서드 (조회수 증가, 태그 점수 증가 없음)
    @Override
    @Transactional(readOnly = true)
    public NewsResponseDto getNewsFeign(UUID id, CurrentUserInfoDto userInfoDto) {
        News news = getNewsByRole(id, userInfoDto.role());
        return NewsResponseDto.from(news);
    }
}

