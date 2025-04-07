package com.newpick4u.news.news.infrastructure.kafka;

import com.newpick4u.news.news.application.usecase.NewsMessageHandler;
import com.newpick4u.news.news.infrastructure.kafka.dto.NewsInfoDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RetryNewsInfoConsumer {

    private final NewsMessageHandler newsMessageHandler;

    @KafkaListener(
            topics = "news-info.dlq.v1",
            groupId = "news-info-dlq-consumer",
            containerFactory = "newsTagListenerContainerFactory"
    )
    public void consume(ConsumerRecord<String, NewsInfoDto> record) {
        NewsInfoDto dto = record.value();
        try {
            log.info("[DLQ Retry] 뉴스 초안 DLQ 재처리 시작: {}", dto);
            newsMessageHandler.handleNewsInfoCreate(dto);

        } catch (Exception e) {
            log.error("[DLQ Retry] 뉴스 초안 재처리 실패 - 메시지: {}", dto, e);
            throw new RuntimeException(e);
        }
    }
}
