package com.newpick4u.news;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.newpick4u.news.news.application.dto.NewsTagDto;
import com.newpick4u.news.news.application.usecase.TagInboxScheduler;
import com.newpick4u.news.news.domain.entity.News;
import com.newpick4u.news.news.domain.entity.TagInbox;
import com.newpick4u.news.news.domain.repository.NewsRepository;
import com.newpick4u.news.news.domain.repository.TagInboxRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.UUID;

@SpringBootTest(properties = {
        "eureka.client.enabled=false"
})
@ActiveProfiles("test")
class TagInboxSchedulerTest {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TagInboxScheduler scheduler;

    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private TagInboxRepository tagInboxRepository;

    @Test
    void 인박스_태그_스케줄링_적용_테스트() throws Exception {
        // given: 뉴스 + 태그 인박스 준비
        String aiNewsId = "inbox-news-" + UUID.randomUUID();
        News news = News.create(aiNewsId, "스케줄러 뉴스", "내용", "https://example.com", "2025-04-15", 0L);
        newsRepository.save(news);

        NewsTagDto dto = new NewsTagDto(aiNewsId, List.of(
                new NewsTagDto.TagDto(UUID.randomUUID(), "스케줄태그")
        ));
        String json = objectMapper.writeValueAsString(dto);

        TagInbox inbox = TagInbox.create(aiNewsId, json);
        tagInboxRepository.save(inbox);

        // when: 스케줄러 수동 실행
        scheduler.processInbox();

        // then: 태그 적용 확인 + 인박스 비었는지 확인
        News updated = newsRepository.findWithTagsByAiNewsId(aiNewsId).orElseThrow();
        assertThat(updated.getNewsTagList()).hasSize(1);
        assertThat(tagInboxRepository.findAll()).isEmpty();
    }
}
