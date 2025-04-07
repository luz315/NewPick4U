package com.newpick4u.news.news.application.usecase;

import com.newpick4u.news.news.infrastructure.kafka.dto.NewsInfoDto;
import com.newpick4u.news.news.infrastructure.kafka.dto.NewsTagDto;

public interface NewsService {
    void createNewsInfo(NewsInfoDto dto);
    void updateTagList(NewsTagDto dto);
    }
