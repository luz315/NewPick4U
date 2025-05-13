package com.newpick4u.news.news.application.dto;

public record NewsSearchCriteria(
        String keyword,
        String sort,
        String filter,
        int page,
        int size
) {
}
