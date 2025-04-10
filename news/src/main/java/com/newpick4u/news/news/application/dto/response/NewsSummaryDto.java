package com.newpick4u.news.news.application.dto.response;

import com.newpick4u.news.news.domain.entity.News;
import com.newpick4u.news.news.domain.entity.NewsStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record NewsSummaryDto(
    UUID id,
    String title,
    String content,
    String url,
    String publishedDate,
    NewsStatus status,
    long view,
    LocalDateTime createdAt,
    List<NewsTagResponseDto> tags
) {
    public static NewsSummaryDto from(News news) {
        return new NewsSummaryDto(
            news.getId(),
            news.getTitle(),
            news.getContent(),
            news.getUrl(),
            news.getPublishedDate(),
            news.getStatus(),
            news.getView(),
            news.getCreatedAt(),
            news.getNewsTagList().stream()
                .map(NewsTagResponseDto::from)
                .toList()
        );
    }
}
