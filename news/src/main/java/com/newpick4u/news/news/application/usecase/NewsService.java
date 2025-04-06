package com.newpick4u.news.news.application.usecase;

import com.newpick4u.news.news.infrastructure.kafka.dto.AiNewsDto;

public interface NewsService {
    void createNewsFromAi(AiNewsDto dto);
}
