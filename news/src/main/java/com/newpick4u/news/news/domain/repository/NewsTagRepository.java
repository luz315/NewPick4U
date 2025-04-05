package com.newpick4u.news.news.domain.repository;

import com.newpick4u.news.news.domain.entity.News;

public interface NewsTagRepository {
    News save(News news);
}