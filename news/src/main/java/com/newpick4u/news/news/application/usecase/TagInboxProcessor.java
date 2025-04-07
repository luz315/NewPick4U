package com.newpick4u.news.news.application.usecase;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.newpick4u.news.news.domain.entity.News;
import com.newpick4u.news.news.domain.entity.NewsTag;
import com.newpick4u.news.news.domain.entity.TagInbox;
import com.newpick4u.news.news.infrastructure.kafka.dto.NewsTagDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TagInboxProcessor {

    private final ObjectMapper objectMapper;

    public void applyTagFromInbox(News news, TagInbox inbox) {
        try {
            NewsTagDto dto = objectMapper.readValue(inbox.getJsonPayload(), NewsTagDto.class);

            List<NewsTag> tags = dto.tagList().stream()
                    .map(tag -> NewsTag.create(tag.id(), tag.name(), news))
                    .toList();

            news.updateNewsTags(tags);
        } catch (Exception e) {
            throw new RuntimeException("인박스 메시지 처리 실패", e);
        }
    }
}
