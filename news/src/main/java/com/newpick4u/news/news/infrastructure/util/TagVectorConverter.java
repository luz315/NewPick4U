package com.newpick4u.news.news.infrastructure.util;

import com.newpick4u.news.news.domain.entity.News;
import com.newpick4u.news.news.domain.entity.UserTagLog;

import java.util.*;
import java.util.stream.Collectors;

public class TagVectorConverter {
    public static Set<String> extractGlobalTagSetFromNews(UserTagLog userLog, List<News> newsList) {
        Set<String> tagSet = new HashSet<>(userLog.getTagCount().keySet());
        for (News news : newsList) {
            news.getNewsTagList().forEach(tag -> tagSet.add(tag.getName()));
        }
        return tagSet;
    }

    /**
     * 전체 사용자 태그 로그에서 전역 태그 인덱스 추출
     */
    public static Set<String> extractGlobalTagSet(List<UserTagLog> allLogs) {
        Set<String> globalTags = new HashSet<>();
        for (UserTagLog log : allLogs) {
            globalTags.addAll(log.getTagCount().keySet());
        }
        return globalTags;
    }

    /**
     * 사용자 태그 벡터화
     */
    public static double[] toVector(UserTagLog log, List<String> tagIndexList) {
        double[] vector = new double[tagIndexList.size()];
        Map<String, Integer> tagCountMap = log.getTagCount();

        for (int i = 0; i < tagIndexList.size(); i++) {
            vector[i] = tagCountMap.getOrDefault(tagIndexList.get(i), 0);
        }

        return vector;
    }

    /**
     * 뉴스 태그 벡터화 (1 또는 0)
     */
    public static double[] toVector(News news, List<String> tagIndexList) {
        double[] vector = new double[tagIndexList.size()];
        Set<String> tags = news.getNewsTagList().stream().map(tag -> tag.getName()).collect(Collectors.toSet());
        for (int i = 0; i < tagIndexList.size(); i++) {
            vector[i] = tags.contains(tagIndexList.get(i)) ? 1.0 : 0.0;
        }
        return vector;
    }
}
