package com.newpick4u.news.news.infrastructure.util;

import com.newpick4u.news.news.application.usecase.NewsRecommender;
import com.newpick4u.news.news.infrastructure.redis.NewsVectorQueueOperatorImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class NewsRecommenderImpl implements NewsRecommender {

    private final NewsVectorQueueOperatorImpl newsVectorQueueOperator;

    @Override
    public List<UUID> recommendByContentVector(
            double[] userVector,
            List<UUID> candidates
    ) {
        return candidates.stream()
                .map(newsId -> {
                    Optional<double[]> vectorOpt = newsVectorQueueOperator.getVector(newsId);

                    return vectorOpt.map(vec -> Map.entry(newsId,
                                    CosineSimilarityUtil.cosineSimilarity(userVector, vec)))
                            .orElseGet(() -> {
                                log.warn("[뉴스 벡터 없음] 캐시 누락 → newsId={}", newsId);
                                return null;
                            });
                })
                .filter(Objects::nonNull)
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue())) // 유사도 내림차순
                .limit(10)
                .map(Map.Entry::getKey)
                .toList();
    }
}