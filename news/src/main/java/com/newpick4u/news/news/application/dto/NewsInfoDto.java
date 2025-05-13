package com.newpick4u.news.news.application.dto;

public record NewsInfoDto(
    String aiNewsId,
    String title,
    String content,
    String url,
    String publishedDate
) {}