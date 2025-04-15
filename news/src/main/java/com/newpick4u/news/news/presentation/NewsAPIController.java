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

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/news")
@RequiredArgsConstructor
public class NewsAPIController {
    private final NewsService newsService;

    // 추천 뉴스 조회
    @GetMapping("/recommend")
    public List<NewsSummaryDto> recommend(@CurrentUserInfo CurrentUserInfoDto currentUser) {
        return newsService.recommendTop10(currentUser);
    }
}
