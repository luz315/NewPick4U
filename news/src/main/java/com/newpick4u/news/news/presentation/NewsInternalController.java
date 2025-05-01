package com.newpick4u.news.news.presentation;

import com.newpick4u.common.resolver.annotation.CurrentUserInfo;
import com.newpick4u.common.resolver.dto.CurrentUserInfoDto;
import com.newpick4u.news.news.application.dto.response.NewsResponseDto;
import com.newpick4u.news.news.application.usecase.NewsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.newpick4u.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/internal/v1/news")
@RequiredArgsConstructor
@Tag(name = "뉴스 INTERNAL", description = "뉴스 INTERNAL")
public class NewsInternalController {
    private final NewsService newsService;

    @GetMapping("/{id}")
    @Operation(summary = "뉴스 조회", description = "특정 뉴스를 조회합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "뉴스 조회 성공")
    public ResponseEntity<ApiResponse<NewsResponseDto>> getNews(
            @PathVariable UUID id,
            @CurrentUserInfo @Parameter(hidden = true) CurrentUserInfoDto userInfoDto
    ) {
        return ResponseEntity.ok(ApiResponse.ok(newsService.getNews(id, userInfoDto)));
    }

    @GetMapping("/exist/{id}")
    @Operation(
            summary = "뉴스 단순 조회 (내부용)",
            description = "조회수, 사용자 태그 점수 증가 없이 뉴스만 조회"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "뉴스 조회 성공")
    public ResponseEntity<ApiResponse<NewsResponseDto>> getNewsForFeign(
            @PathVariable UUID id,
            @CurrentUserInfo @Parameter(hidden = true) CurrentUserInfoDto userInfoDto
    ) {
        NewsResponseDto response = newsService.getNewsFeign(id, userInfoDto);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
