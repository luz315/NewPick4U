package com.newpick4u.news.news.application.usecase;

import com.newpick4u.news.news.domain.entity.News;

import java.util.List;
import java.util.UUID;

public interface NewsRecommender {
   List<UUID> recommendByContentVector( double[] userVector,
                                        List<UUID> candidates
   );
}
