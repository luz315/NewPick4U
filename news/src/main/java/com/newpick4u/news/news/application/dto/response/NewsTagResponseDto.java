package com.newpick4u.news.news.application.dto.response;

import com.newpick4u.news.news.domain.entity.NewsTag;

import java.util.UUID;

public record NewsTagResponseDto(
    UUID id,
    String name
) {
    public static NewsTagResponseDto from(NewsTag newsTag) {
        return new NewsTagResponseDto(
                newsTag.getId(),
                newsTag.getName()
        );
    }
}
