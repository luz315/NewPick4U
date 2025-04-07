package com.newpick4u.newsorigin.newsorigin.domain.repository;

import com.newpick4u.newsorigin.newsorigin.domain.entity.NewsOrigin;
import java.util.List;

public interface NewsOriginRepository {

  NewsOrigin save(NewsOrigin newsOrigin);

  List<NewsOrigin> saveAll(List<NewsOrigin> newsOriginList);

  List<NewsOrigin> getAllByBeforeSentQueue();
}
