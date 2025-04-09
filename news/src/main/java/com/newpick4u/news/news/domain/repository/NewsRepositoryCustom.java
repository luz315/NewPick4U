package com.newpick4u.news.news.domain.repository;

import com.newpick4u.news.news.application.dto.response.NewsResponseDto;
import com.newpick4u.news.news.domain.critria.NewsSearchCriteria;
import com.newpick4u.news.news.domain.entity.News;
import com.newpick4u.news.news.domain.model.Pagination;

import java.util.List;

public interface NewsRepositoryCustom {
    Pagination<News> searchNewsList(NewsSearchCriteria request);
}
