package com.newpick4u.news.news.application.scheduler;

import com.newpick4u.common.exception.CustomException;
import com.newpick4u.news.global.exception.NewsErrorCode;
import com.newpick4u.news.news.application.usecase.NewsRecommender;
import com.newpick4u.news.news.application.usecase.RecommendationCacheOperator;
import com.newpick4u.news.news.application.usecase.VectorConverter;
import com.newpick4u.news.news.domain.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Component
@RequiredArgsConstructor
public class RecommendScheduler {

    private final NewsRepository newsRepository;
    private final RecommendationCacheOperator recommendationCacheOperator;
    private final VectorConverter vectorConverterProvider;
    private final NewsRecommender newsRecommender;
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    @Scheduled(cron = "0 0 3 * * *")
    public void updateAllUserRecommendations() {
        log.info("[배치 시작] 사용자 추천 뉴스 계산");

        List<Long> userIds = new ArrayList<>(recommendationCacheOperator.getCachedUserIds());
        List<UUID> candidateIds = newsRepository.findAllActiveNewsIds();

        for (Long userId : userIds) {
            executorService.submit(() -> updateSingleUserRecommendation(userId, candidateIds));
        }
    }

    private void updateSingleUserRecommendation(Long userId, List<UUID> candidateIds) {
        try {
            // 사용자 태그 로그 점수표 가져오기
            Map<String, Double> userTagMap = recommendationCacheOperator.getUserTagScore(userId);
            if (userTagMap.isEmpty()) return;


            // 전역 태그 인덱스는 Redis에서 가져오기
            List<String> tagIndexList = recommendationCacheOperator.getGlobalTagIndexList();
            if (tagIndexList.isEmpty()) return;

            // 1. 벡터화 (only 전역태그 인덱스, 사용자 태그로그 / 뉴스 벡터화는 스케줄러가 진행)
            double[] userVector = vectorConverterProvider.toUserVector(userTagMap, tagIndexList);

            // 2. 유사도 계산
            List<UUID> recommended = newsRecommender.recommendByContentVector(userVector, candidateIds);

            // 3. Redis에 캐싱
            List<String> recommendedIds = recommended.stream()
                    .map(UUID::toString) // newsId 자체일 경우
                    .toList();

            if (recommended.isEmpty()) {
                log.info("[추천 없음] userId={}", userId);
                return;
            }

            recommendationCacheOperator.storeRecommendedNews(userId, recommendedIds);
            log.info("[추천 저장 완료] userId={}, count={}", userId, recommendedIds.size());
        } catch (Exception e) {
            log.error("[추천 저장 실패] userId={}", userId, e);
            throw CustomException.from(NewsErrorCode.NEWS_RECOMMENDATION_FAIL);
        }
    }
}