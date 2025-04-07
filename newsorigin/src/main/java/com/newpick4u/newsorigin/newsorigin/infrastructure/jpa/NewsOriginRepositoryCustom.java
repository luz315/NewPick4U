package com.newpick4u.newsorigin.newsorigin.infrastructure.jpa;

import com.newpick4u.newsorigin.newsorigin.domain.entity.NewsOrigin;
import java.util.List;

public interface NewsOriginRepositoryCustom {

  List<NewsOrigin> getAllByBeforeSentQueue();
}
