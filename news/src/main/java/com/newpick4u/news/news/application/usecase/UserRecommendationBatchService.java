package com.newpick4u.news.news.application.usecase;

import com.newpick4u.news.news.domain.entity.News;
import com.newpick4u.news.news.domain.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserRecommendationBatchService {

    private final NewsRepository newsRepository;
    private final TagLogCacheOperator tagLogCacheOperator;
    private final TagVectorConverter tagVectorConverterProvider;
    private final NewsRecommender newsRecommender;
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    @Scheduled(cron = "0 0 3 * * *")
    public void updateAllUserRecommendations() {
        log.info("[배치 시작] 사용자 추천 뉴스 계산");

        List<Long> userIds = new ArrayList<>(tagLogCacheOperator.getAllUserIds());
        List<News> allNews = newsRepository.findAllActive();

        for (Long userId : userIds) {
            executorService.submit(() -> updateSingleUserRecommendation(userId, allNews));
        }
    }

    private void updateSingleUserRecommendation(Long userId, List<News> allNews) {
        try {
            Map<String, Double> userTagMap = tagLogCacheOperator.getUserTagScoreMap(userId);
            if (userTagMap.isEmpty()) return;

            Set<String> globalTagSet = tagVectorConverterProvider.extractGlobalTagSetFromNews(userTagMap, allNews);
            List<String> tagIndexList = new ArrayList<>(globalTagSet);
            // 1. 벡터화
            double[] userVector = tagVectorConverterProvider.toUserVector(userTagMap, tagIndexList);

            // 2. 유사도 계산
            List<News> recommended = newsRecommender.recommendByContentVector(userVector, allNews, tagIndexList);

            // 3. Redis에 캐싱
            List<String> recommendedIds = recommended.stream()
                    .map(news -> news.getId().toString())
                    .collect(Collectors.toList());

            tagLogCacheOperator.cacheRecommendedNews(userId, recommendedIds);
            log.info("[추천 저장 완료] userId={}, count={}", userId, recommendedIds.size());
        } catch (Exception e) {
            log.error("[추천 저장 실패] userId={}", userId, e);
        }
    }
}