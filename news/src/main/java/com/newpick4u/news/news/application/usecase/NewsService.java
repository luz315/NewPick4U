package com.newpick4u.news.news.application.usecase;

import com.newpick4u.common.resolver.dto.CurrentUserInfoDto;
import com.newpick4u.news.news.application.dto.NewsInfoDto;
import com.newpick4u.news.news.application.dto.NewsTagDto;
import com.newpick4u.news.news.application.dto.response.NewsResponseDto;
import com.newpick4u.news.news.application.dto.response.NewsSummaryDto;
import com.newpick4u.news.news.application.dto.response.PageResponse;
import com.newpick4u.news.news.application.dto.NewsSearchCriteria;

import java.util.List;
import java.util.UUID;

public interface NewsService {
    void saveNewsInfo(NewsInfoDto dto);
    void updateNewsTagList(NewsTagDto dto);
    NewsResponseDto getNews(UUID id, CurrentUserInfoDto userInfo);
    PageResponse<NewsSummaryDto> searchNewsList(NewsSearchCriteria request, CurrentUserInfoDto userInfoDto);
    List<NewsSummaryDto> recommendTop10(CurrentUserInfoDto user);
}