package com.newpick4u.news.news.infrastructure.feign.dto;

import java.util.UUID;

public record TagDto(
    UUID id,
    String name
) {}

