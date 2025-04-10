package com.newpick4u.news.news.application.dto.response;

import com.newpick4u.news.news.domain.model.Pagination;
import lombok.Builder;

import java.util.List;
import java.util.function.Function;

@Builder
public record PageResponse<T>(
    List<T> contents,
    long totalElements,
    int totalPages,
    int pageNumber,
    int pageSize
) {

    public static <T> PageResponse<T> from(Pagination<T> pagination) {
        return PageResponse.<T>builder()
            .contents(pagination.getContents())
            .totalElements(pagination.getTotalElements())
            .totalPages(pagination.getTotalPages())
            .pageNumber(pagination.getPageNumber())
            .pageSize(pagination.getPageSize())
            .build();
    }

    public <U> PageResponse<U> map(Function<T, U> mapper) {
        List<U> mapped = this.contents.stream()
            .map(mapper)
            .toList();

        return PageResponse.<U>builder()
            .contents(mapped)
            .totalElements(this.totalElements)
            .totalPages(this.totalPages)
            .pageNumber(this.pageNumber)
            .pageSize(this.pageSize)
            .build();
    }
}
