package com.newpick4u.news.news.presentation;

import com.newpick4u.news.news.application.dto.response.NewsListResponse;
import com.newpick4u.news.news.application.dto.response.NewsResponseDto;
import com.newpick4u.news.news.application.usecase.NewsService;
import com.newpick4u.news.news.domain.critria.NewsSearchCriteria;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/news")
@RequiredArgsConstructor
public class NewsController {
    private final NewsService newsService;

    @GetMapping("/{id}")
    public ResponseEntity<NewsResponseDto> getNews(@PathVariable UUID id) {
        return ResponseEntity.ok(newsService.getNews(id));
    }

    @GetMapping
    public ResponseEntity<NewsListResponse> searchNewsList(
            @ModelAttribute NewsSearchCriteria request
    ) {
        return ResponseEntity.ok(newsService.searchNewsList(request));
    }
}
