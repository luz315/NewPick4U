package com.newpick4u.news.news.application.dto.response;

import com.newpick4u.news.news.domain.entity.News;
import com.newpick4u.news.news.domain.entity.NewsStatus;
import com.newpick4u.news.news.domain.model.Pagination;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record NewsListResponse(
        int page,
        int size,
        long totalElements,
        long totalPages,
        List<NewsSummary> contents
) {
    public static NewsListResponse from(Pagination<News> pagination) {
        int size = pagination.getSize();
        return new NewsListResponse(
                pagination.getPage(),
                size,
                pagination.getTotal(),
                (pagination.getTotal() + size - 1) / size,
                pagination.getContent().stream().map(NewsSummary::from).toList()
        );
    }

    public record NewsSummary(
            UUID id,
            String title,
            String content,
            NewsStatus status,
            long view,
            LocalDateTime createdAt,
            List<NewsTagResponseDto> tags
    ) {
        public static NewsSummary from(News news) {
            return new NewsSummary(
                    news.getId(),
                    news.getTitle(),
                    news.getContent(),
                    news.getStatus(),
                    news.getView(),
                    news.getCreatedAt(),
                    news.getNewsTagList().stream()
                            .map(NewsTagResponseDto::from)
                            .toList()
            );
        }
    }
}
