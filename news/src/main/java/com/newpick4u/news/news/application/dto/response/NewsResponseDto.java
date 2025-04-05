package com.newpick4u.news.news.application.dto.response;

import com.newpick4u.news.news.domain.entity.News;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public record NewsResponseDto(
    UUID id,
    String title,
    String content,
    List<NewsTagResponseDto> newsTagList
//    LocalDateTime createdAt,
//    String createdBy,
//    LocalDateTime updatedAt,
//    String updatedBy,
//    LocalDateTime deletedAt,
//    String deletedBy
) {
    public static NewsResponseDto from(
        News news
    ) {
        return new NewsResponseDto(
            news.getId(),
            news.getTitle(),
            news.getContent(),
            news.getNewsTagList().stream().map(NewsTagResponseDto::from)
                                .collect(Collectors.toList())

//            news.getCreatedAt(),
//            news.getCreatedBy(),
//            news.getUpdatedAt(),
//            news.getUpdatedBy(),
//            news.getDeletedAt(),
//            news.getDeletedBy()
        );
    }
}
