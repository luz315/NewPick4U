package com.newpick4u.news.news.application.usecase;

import com.newpick4u.news.news.domain.repository.NewsRepository;
import com.newpick4u.news.news.domain.repository.TagInboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class TagInboxScheduler {

    private final NewsRepository newsRepository;
    private final TagInboxRepository tagInboxRepository;
    private final TagInboxProcessor tagInboxProcessor;

    @Scheduled(fixedDelay = 10_000) // 10초마다 실행
    @Transactional
    public void processInbox() {
        var inboxList = tagInboxRepository.findAll();

        for (var inbox : inboxList) {
            var aiNewsId = inbox.getAiNewsId();

            newsRepository.findByAiNewsId(aiNewsId).ifPresent(news -> {
                try {
                    tagInboxProcessor.applyTagFromInbox(news, inbox);
                    tagInboxRepository.delete(inbox);
                    log.info("인박스 태그 적용 완료 - aiNewsId: {}", aiNewsId);
                } catch (Exception e) {
                    log.warn("인박스 처리 실패 - aiNewsId: {}", aiNewsId, e);
                }
            });
        }
    }
}
