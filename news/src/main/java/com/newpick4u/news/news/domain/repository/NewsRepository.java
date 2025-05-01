package com.newpick4u.news.news.domain.repository;

import com.newpick4u.news.news.application.dto.NewsSearchCriteria;
import com.newpick4u.news.news.domain.entity.News;
import com.newpick4u.news.news.domain.model.Pagination;
import com.newpick4u.news.news.domain.repository.projection.NewsCreatedInfo;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NewsRepository  {
    News save(News news);
    List<News> findAll();
    Optional<News> findByAiNewsId(String aiNewsId);
    Optional<News> findWithTagsByAiNewsId(String aiNewsId);
    boolean existsByAiNewsId(String aiNewsId);
    Pagination<News> searchNewsList(NewsSearchCriteria request, boolean isMaster);
    Optional<News> findNewsByRole(UUID id, boolean isMaster);
    void flush();
    List<UUID> findAllActiveNewsIds();
    List<News> findLatestNews(int limit);
    List<News> findByIds(List<UUID> ids);
    List<News> saveAll(List<News> newsList);
    void deleteAll();
    Optional<News> findById(UUID id);
    void incrementViewCount(UUID newsId, long count);
    List<NewsCreatedInfo> findAllActiveNewsCreatedInfos();
}