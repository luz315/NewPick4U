package com.newpick4u.news.news.application.dto.response;

import com.newpick4u.news.news.application.dto.response.NewsTagResponseDto;
import com.newpick4u.news.news.domain.entity.News;
import com.newpick4u.news.news.domain.entity.NewsStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public record NewsResponseDto(
    UUID id,
    String title,
    String content,
    String url,
    String publishedDate,
    NewsStatus status,
    List<NewsTagResponseDto> newsTagList,
    Long createdBy,
    LocalDateTime createdAt,
    Long updatedBy,
    LocalDateTime updatedAt,
    Long deletedBy,
    LocalDateTime deletedAt

) {
    public static NewsResponseDto from(News news) {
        return new NewsResponseDto(
            news.getId(),
            news.getTitle(),
            news.getContent(),
            news.getUrl(),
            news.getPublishedDate(),
            news.getStatus(),
            news.getNewsTagList().stream().map(NewsTagResponseDto::from)
                                .collect(Collectors.toList()),

            news.getCreatedBy(),
            news.getCreatedAt(),
            news.getUpdatedBy(),
            news.getUpdatedAt(),
            news.getDeletedBy(),
            news.getDeletedAt()
        );
    }
}
