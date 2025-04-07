package com.newpick4u.news.news.application.usecase;

import com.newpick4u.news.news.infrastructure.kafka.dto.NewsInfoDto;
import com.newpick4u.news.news.infrastructure.kafka.dto.NewsTagDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NewsMessageHandler {

    private final NewsService newsService;

    public void handleNewsInfoCreate(NewsInfoDto dto) {
        newsService.createNewsInfo(dto);
    }

    public void handleNewsTagUpdate(NewsTagDto dto) {
        newsService.updateTagList(dto);
    }
}