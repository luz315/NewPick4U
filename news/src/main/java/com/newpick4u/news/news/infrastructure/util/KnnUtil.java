package com.newpick4u.news.news.infrastructure.util;

import com.newpick4u.news.news.domain.entity.UserTagLog;

import java.util.*;
import java.util.stream.Collectors;

/**
 * KNN 기반으로 유사한 사용자 탐색을 위한 유틸 클래스
 */
public class KnnUtil {

    /**
     * 현재 사용자 벡터와 전체 사용자 벡터들 간의 유사도를 계산해
     * 상위 K개의 유사한 사용자 ID(Long)를 반환한다.
     *
     * @param targetVector 현재 사용자 벡터
     * @param otherVectors 다른 사용자 벡터 리스트
     * @param allLogs 벡터에 해당하는 사용자 로그 리스트 (순서 동일해야 함)
     * @param k 최상위 이웃 수
     * @return 유사한 사용자 ID 리스트
     */
    public static List<Long> findTopKNeighbors(
            double[] targetVector,
            List<double[]> otherVectors,
            List<UserTagLog> allLogs,
            int k
    ) {
        List<AbstractMap.SimpleEntry<Long, Double>> similarities = new ArrayList<>();

        for (int i = 0; i < otherVectors.size(); i++) {
            double similarity = VectorSimilarityCalculator.cosineSimilarity(targetVector, otherVectors.get(i));
            Long userId = allLogs.get(i).getUserId();
            similarities.add(new AbstractMap.SimpleEntry<>(userId, similarity));
        }

        return similarities.stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue())) // 유사도 내림차순
                .limit(k)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
}
