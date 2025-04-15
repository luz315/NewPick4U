package com.newpick4u.news.news.infrastructure.util;

import com.newpick4u.news.news.domain.entity.News;
import com.newpick4u.news.news.domain.entity.UserTagLog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class NewsRecommender {

    public static List<News> recommendByContentVector(
            double[] userVector,
            List<News> candidates,
            List<String> tagIndexList
    ) {
        return candidates.stream()
                .map(news -> new AbstractMap.SimpleEntry<>(
                        news,
                        VectorSimilarityCalculator.cosineSimilarity(
                                userVector,
                                TagVectorConverter.toNewsVector(news, tagIndexList)
                        )))
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue())) // 유사도 내림차순
                .limit(10)
                .map(Map.Entry::getKey)
                .toList();
    }
}  