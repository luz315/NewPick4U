package com.newpick4u.news.news.domain.repository;

import com.newpick4u.news.news.domain.critria.NewsSearchCriteria;
import com.newpick4u.news.news.domain.entity.News;
import com.newpick4u.news.news.domain.model.Pagination;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NewsRepository  {
    News save(News news);
    Optional<News> findByAiNewsId(String aiNewsId);
    Optional<News> findWithTagsByAiNewsId(String aiNewsId);
    boolean existsByAiNewsId(String aiNewsId);
    Pagination<News> searchNewsList(NewsSearchCriteria request, boolean isMaster);
    Optional<News> findNewsByRole(UUID id, boolean isMaster);
    void flush();
    List<News> findAllActive();
    List<News> findLatestNews(int limit);
    List<News> findByIds(List<UUID> ids);
}