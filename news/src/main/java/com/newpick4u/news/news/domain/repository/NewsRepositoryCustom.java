package com.newpick4u.news.news.domain.repository;

import com.newpick4u.news.news.domain.critria.NewsSearchCriteria;
import com.newpick4u.news.news.domain.entity.News;
import com.newpick4u.news.news.domain.model.Pagination;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NewsRepositoryCustom {
    Pagination<News> searchNewsList(NewsSearchCriteria request, boolean isMaster);
    Optional<News> findNewsByRole(UUID id, boolean isMaster);
    List<News> findAllActive();
}
