package com.newpick4u.news.news.presentation;

import com.newpick4u.common.resolver.annotation.CurrentUserInfo;
import com.newpick4u.common.resolver.dto.CurrentUserInfoDto;
import com.newpick4u.news.news.application.dto.response.NewsResponseDto;
import com.newpick4u.news.news.application.dto.response.NewsSummaryDto;
import com.newpick4u.news.news.application.dto.response.PageResponse;
import com.newpick4u.news.news.application.usecase.NewsService;
import com.newpick4u.news.news.domain.critria.NewsSearchCriteria;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/news")
@RequiredArgsConstructor
public class NewsAPIController {
    private final NewsService newsService;

    @GetMapping("/{id}")
    public ResponseEntity<NewsResponseDto> getNews(
            @PathVariable UUID id,
            @CurrentUserInfo CurrentUserInfoDto userInfoDto
    ) {
        return ResponseEntity.ok(newsService.getNews(id, userInfoDto));
    }

    @GetMapping
    public ResponseEntity<PageResponse<NewsSummaryDto>> searchNewsList(
            @ModelAttribute NewsSearchCriteria request,
            @CurrentUserInfo CurrentUserInfoDto userInfoDto
    ) {
        return ResponseEntity.ok(newsService.searchNewsList(request, userInfoDto));
    }
}
