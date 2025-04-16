package com.newpick4u.news.news.infrastructure.util;

import com.newpick4u.news.news.application.usecase.NewsRecommenderProvider;
import com.newpick4u.news.news.application.usecase.TagVectorConverterProvider;
import com.newpick4u.news.news.domain.entity.News;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class NewsRecommender implements NewsRecommenderProvider {

    private final TagVectorConverterProvider tagVectorConverterProvider;

    @Override
    public List<News> recommendByContentVector(
            double[] userVector,
            List<News> candidates,
            List<String> tagIndexList
    ) {
        return candidates.stream()
                .map(news -> new AbstractMap.SimpleEntry<>(
                        news,
                        VectorSimilarityCalculator.cosineSimilarity(
                                userVector,
                                tagVectorConverterProvider.toNewsVector(news, tagIndexList)
                        )))
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue())) // 유사도 내림차순
                .limit(10)
                .map(Map.Entry::getKey)
                .toList();
    }
}  