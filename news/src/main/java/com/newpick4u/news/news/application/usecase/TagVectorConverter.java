package com.newpick4u.news.news.application.usecase;

import com.newpick4u.news.news.domain.entity.News;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface TagVectorConverter {
    Set<String> extractGlobalTagSetFromNews(Map<String, Double> userTagMap, List<News> newsList);
    double[] toUserVector(Map<String, Double> tagScoreMap, List<String> tagIndexList);
    double[] toNewsVector(News news, List<String> tagIndexList);
}
