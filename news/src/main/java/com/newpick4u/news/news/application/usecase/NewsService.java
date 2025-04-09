package com.newpick4u.news.news.application.usecase;

import com.newpick4u.news.news.application.dto.NewsInfoDto;
import com.newpick4u.news.news.application.dto.NewsTagDto;
import com.newpick4u.news.news.application.dto.response.NewsListResponse;
import com.newpick4u.news.news.application.dto.response.NewsResponseDto;
import com.newpick4u.news.news.domain.critria.NewsSearchCriteria;

import java.util.UUID;

public interface NewsService {
    void saveNewsInfo(NewsInfoDto dto);
    void updateNewsTagList(NewsTagDto dto);
    NewsResponseDto getNews(UUID id);
    NewsListResponse searchNewsList(NewsSearchCriteria request);
}