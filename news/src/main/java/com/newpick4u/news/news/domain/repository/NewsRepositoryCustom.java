package com.newpick4u.news.news.domain.repository;

import com.newpick4u.news.news.application.dto.NewsSearchCriteria;
import com.newpick4u.news.news.domain.entity.News;
import com.newpick4u.news.news.domain.model.Pagination;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NewsRepositoryCustom {
    Pagination<News> searchNewsList(NewsSearchCriteria request, boolean isMaster);

    Optional<News> findNewsByRole(UUID id, boolean isMaster);

    Optional<News> findWithTagsByAiNewsId(String aiNewsId);

    List<UUID> findAllActiveNewsIds();

    List<News> findLatestNews(int limit);

    List<News> findByIds(List<UUID> ids);

    Optional<News> findWithTagsById(UUID id);
}