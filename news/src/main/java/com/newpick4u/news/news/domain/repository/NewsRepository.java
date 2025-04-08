package com.newpick4u.news.news.domain.repository;

import com.newpick4u.news.news.domain.entity.News;

import java.util.Optional;

public interface NewsRepository {
    News save(News news);
    Optional<News> findByAiNewsId(String aiNewsId);
    boolean existsByAiNewsId(String aiNewsId);
}