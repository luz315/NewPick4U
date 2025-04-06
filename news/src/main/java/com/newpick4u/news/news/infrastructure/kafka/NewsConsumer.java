package com.newpick4u.news.news.infrastructure.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.newpick4u.news.news.application.usecase.NewsMessageHandler;
import com.newpick4u.news.news.infrastructure.kafka.dto.AiNewsDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NewsConsumer {

    private final NewsMessageHandler newsMessageHandler;

    @KafkaListener(topics = "dev.news.fct.news.1", groupId = "${KAFKA_GROUP_ID}")
    public void consume(ConsumerRecord<String, AiNewsDto> record, Acknowledgment acknowledgment) {
        AiNewsDto dto = record.value();
        try {
            newsMessageHandler.handle(dto);
            acknowledgment.acknowledge();

        } catch (Exception e) {
            log.error("[Kafka] 뉴스 저장 실패 - 메시지: {}", dto, e);
        }
    }
}
