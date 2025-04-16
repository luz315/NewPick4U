package com.newpick4u.news.news.application.usecase;

import com.newpick4u.news.news.domain.entity.News;

import java.util.List;

public interface NewsRecommender {
   List<News> recommendByContentVector( double[] userVector,
                                        List<News> candidates,
                                        List<String> tagIndexList
   );
}
