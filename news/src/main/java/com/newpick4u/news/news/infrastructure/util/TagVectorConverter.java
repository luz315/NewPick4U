package com.newpick4u.news.news.infrastructure.util;

import com.newpick4u.news.news.domain.entity.News;

import java.util.*;
import java.util.stream.Collectors;

public class TagVectorConverter {
    // 사용자 + 뉴스 태그로부터 전역 태그 셋 추출 (추천 벡터 인덱스용)
    public static Set<String> extractGlobalTagSetFromNews(Map<String, Double> userTagMap, List<News> newsList) {
        Set<String> tagSet = new HashSet<>(userTagMap.keySet());
        for (News news : newsList) {
            news.getNewsTagList().forEach(tag -> tagSet.add(tag.getName()));
        }
        return tagSet;
    }

    // 사용자 태그 점수 기반 벡터화
    public static double[] toUserVector(Map<String, Double> tagScoreMap, List<String> tagIndexList) {
        double[] vector = new double[tagIndexList.size()];
        for (int i = 0; i < tagIndexList.size(); i++) {
            vector[i] = tagScoreMap.getOrDefault(tagIndexList.get(i), 0.0);
        }
        return vector;
    }

    // 뉴스 태그 벡터화 (1 또는 0)
    public static double[] toNewsVector(News news, List<String> tagIndexList) {
        double[] vector = new double[tagIndexList.size()];
        Set<String> tags = news.getNewsTagList().stream()
                .map(tag -> tag.getName())
                .collect(Collectors.toSet());
        for (int i = 0; i < tagIndexList.size(); i++) {
            vector[i] = tags.contains(tagIndexList.get(i)) ? 1.0 : 0.0;
        }
        return vector;
    }
}