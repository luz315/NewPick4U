package com.newpick4u.news.news.application.usecase;

import com.newpick4u.news.news.application.dto.NewsInfoDto;
import com.newpick4u.news.news.application.dto.NewsTagDto;

public interface NewsService {
    void saveNewsInfo(NewsInfoDto dto);
    void updateNewsTagList(NewsTagDto dto);
}