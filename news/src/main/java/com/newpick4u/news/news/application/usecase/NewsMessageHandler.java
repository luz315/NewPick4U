package com.newpick4u.news.news.application.usecase;

import com.newpick4u.news.news.infrastructure.kafka.dto.AiNewsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NewsMessageHandler {

    private final NewsService newsService;

    public void handle(AiNewsDto dto) {
        newsService.createNewsFromAi(dto);
    }
}