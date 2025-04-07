package com.newpick4u.news.news.infrastructure.kafka.dto;

public record NewsInfoDto(
    String aiNewsId,
    String title,
    String content
) {}