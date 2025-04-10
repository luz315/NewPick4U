package com.newpick4u.news.news.domain.critria;

public record NewsSearchCriteria(
        String keyword,
        String sort,
        String filter,
        int page,
        int size
) {
}
