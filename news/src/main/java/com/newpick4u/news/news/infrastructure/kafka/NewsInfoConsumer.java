package com.newpick4u.news.news.infrastructure.kafka;

import com.newpick4u.news.news.application.usecase.NewsMessageHandler;
import com.newpick4u.news.news.infrastructure.kafka.dto.NewsInfoDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NewsInfoConsumer {

    private final NewsMessageHandler newsMessageHandler;

    @KafkaListener(
            topics = "news-info.fct.1",
            groupId = "news-info-consumer",
            containerFactory = "newsTagListenerContainerFactory"
    )
    public void consume(ConsumerRecord<String, NewsInfoDto> record, Acknowledgment acknowledgment) {
        try {
            newsMessageHandler.handleNewsInfoCreate(record.value());
            acknowledgment.acknowledge();

        } catch (Exception e) {
            log.error("[Kafka] 뉴스 저장 실패 - 메시지: {}", record.value(), e);
        }
    }
}
