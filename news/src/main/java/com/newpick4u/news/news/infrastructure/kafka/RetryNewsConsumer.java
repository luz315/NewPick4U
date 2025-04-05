package com.newpick4u.news.news.infrastructure.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.newpick4u.news.news.application.usecase.NewsMessageHandler;
import com.newpick4u.news.news.infrastructure.kafka.dto.AiNewsDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RetryNewsConsumer {

    private final NewsMessageHandler newsMessageHandler;

    @KafkaListener(
            topics = "dev.news.dlq.news.1",
            groupId = "news-dlq-consumer"
    )
    public void consume(ConsumerRecord<String, AiNewsDto> record) {
        AiNewsDto dto = record.value();
        try {
            log.info("[DLQ Retry] DLQ 메시지 재처리 시작: {}", dto);
            newsMessageHandler.handle(dto);

        } catch (Exception e) {
            log.error("[DLQ Retry] 재처리 실패 - 메시지: {}", dto, e);
            throw new RuntimeException(e);
        }
    }
}
