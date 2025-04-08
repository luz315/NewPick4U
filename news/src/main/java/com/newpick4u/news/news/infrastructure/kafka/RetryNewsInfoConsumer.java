package com.newpick4u.news.news.infrastructure.kafka;

import com.newpick4u.news.news.application.dto.NewsInfoDto;
import com.newpick4u.news.news.application.usecase.NewsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RetryNewsInfoConsumer {

    private final NewsService newsService;

    @KafkaListener(
            topics = "news-info-dlq.fct.v1",
            groupId = "news-info-dlq-consumer",
            containerFactory = "newsTagListenerContainerFactory"
    )
    public void consume(ConsumerRecord<String, NewsInfoDto> record) {
        NewsInfoDto dto = record.value();
        try {
            log.info("[DLQ Retry] 뉴스 초안 DLQ 재처리 시작: {}", dto);
            newsService.saveNewsInfo(dto);

        } catch (Exception e) {
            log.error("[DLQ Retry] 뉴스 초안 재처리 실패 - 메시지: {}", dto, e);
            throw new RuntimeException(e);
        }
    }
}
