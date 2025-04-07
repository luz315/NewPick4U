package com.newpick4u.news.news.infrastructure.kafka;

import com.newpick4u.news.news.application.usecase.NewsMessageHandler;
import com.newpick4u.news.news.infrastructure.kafka.dto.NewsTagDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RetryNewsTagConsumer {

    private final NewsMessageHandler newsMessageHandler;

    @KafkaListener(
            topics = "tag.dlq.v1",
            groupId = "news-tag-dlq-consumer",
            containerFactory = "newsTagListenerContainerFactory"
    )
    public void consume(ConsumerRecord<String, NewsTagDto> record) {
        NewsTagDto dto = record.value();
        try {
            log.info("[DLQ Retry] 태그 DLQ 재처리 시작: {}", dto);
            newsMessageHandler.handleNewsTagUpdate(dto);
        } catch (Exception e) {
            log.error("[DLQ Retry] 태그 재처리 실패 - 메시지: {}", dto, e);
            throw new RuntimeException(e);
        }
    }
}
