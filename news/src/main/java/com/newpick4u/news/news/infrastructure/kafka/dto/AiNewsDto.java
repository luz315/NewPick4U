package com.newpick4u.news.news.infrastructure.kafka.dto;

import java.util.List;

public record AiNewsDto(
    String title,
    String content,
    List<String> tags
) {}