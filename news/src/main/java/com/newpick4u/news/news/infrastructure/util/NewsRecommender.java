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

    /**
     * Content-Based Filtering 추천 로직
     * @param userLog 사용자 태그 로그
     * @param candidates 전체 뉴스 리스트
     * @return 유사도 기준 상위 10개 뉴스
     */
    public static List<News> recommendContentBased(UserTagLog userLog, List<News> candidates) {
        List<String> tagIndex = new ArrayList<>(TagVectorConverter.extractGlobalTagSetFromNews(userLog, candidates));
        double[] userVector = TagVectorConverter.toVector(userLog, tagIndex);

        return candidates.stream()
                .map(news -> new AbstractMap.SimpleEntry<>(news,
                        VectorSimilarityCalculator.cosineSimilarity(
                                userVector,
                                TagVectorConverter.toVector(news, tagIndex)
                        )))
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .limit(10)
                .map(Map.Entry::getKey)
                .toList();
    }

    /**
     * KNN 방식 추천
     * @param userId 현재 사용자 ID
     * @param userLog 현재 사용자 태그 로그
     * @param allLogs 전체 사용자 태그 로그 목록
     * @param allNews 전체 뉴스 목록
     * @return 추천된 뉴스 리스트
     */
    public static List<News> recommendKnnBased(Long userId, UserTagLog userLog, List<UserTagLog> allLogs, List<News> allNews) {
        allLogs.removeIf(log -> log.getUserId().equals(userId));

        List<String> tagIndex = new ArrayList<>(TagVectorConverter.extractGlobalTagSet(allLogs));
        double[] currentVector = TagVectorConverter.toVector(userLog, tagIndex);

        List<double[]> others = allLogs.stream()
                .map(log -> TagVectorConverter.toVector(log, tagIndex))
                .toList();

        List<Long> neighborIds = KnnUtil.findTopKNeighbors(currentVector, others, allLogs, 5);

        Set<String> recommendedTags = allLogs.stream()
                .filter(log -> neighborIds.contains(log.getUserId()))
                .flatMap(log -> log.getTagCount().keySet().stream())
                .collect(Collectors.toSet());

        return allNews.stream()
                .filter(news -> news.getNewsTagList().stream()
                        .anyMatch(tag -> recommendedTags.contains(tag.getName())))
                .limit(10)
                .toList();
    }
}  
