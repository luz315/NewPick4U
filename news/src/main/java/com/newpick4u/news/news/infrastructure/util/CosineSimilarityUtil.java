package com.newpick4u.news.news.infrastructure.util;

import com.newpick4u.common.exception.CustomException;
import com.newpick4u.news.global.exception.NewsErrorCode;

public class CosineSimilarityUtil {

    /**
     * 코사인 유사도 계산
     * 두 벡터 간의 코사인 유사도를 반환한다. (1에 가까울수록 유사)
     */
    public static double cosineSimilarity(double[] vec1, double[] vec2) {
        if (vec1.length != vec2.length) {
            throw new CustomException(NewsErrorCode.VECTOR_LENGTH_MISMATCH);
        }

        double dotProduct = 0.0;
        double normVec1 = 0.0;
        double normVec2 = 0.0;

        for (int i = 0; i < vec1.length; i++) {
            dotProduct += vec1[i] * vec2[i];
            normVec1 += vec1[i] * vec1[i];
            normVec2 += vec2[i] * vec2[i];
        }

        double denominator = Math.sqrt(normVec1) * Math.sqrt(normVec2);
        return denominator == 0.0 ? 0.0 : dotProduct / denominator;
    }
}