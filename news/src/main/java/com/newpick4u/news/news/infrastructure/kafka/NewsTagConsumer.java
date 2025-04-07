package com.newpick4u.news.news.infrastructure.kafka;

import com.newpick4u.news.news.application.usecase.NewsMessageHandler;
import com.newpick4u.news.news.infrastructure.kafka.dto.NewsTagDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NewsTagConsumer {

    private final NewsMessageHandler newsMessageHandler;

    @KafkaListener(
            topics = "tag.fct.1",
            groupId = "news-tag-consumer",
            containerFactory = "newsTagListenerContainerFactory"
    )
    public void consume(ConsumerRecord<String, NewsTagDto> record, Acknowledgment ack) {
        try {
            newsMessageHandler.handleNewsTagUpdate(record.value());
            ack.acknowledge();
        } catch (Exception e) {
            log.error("[Kafka] 뉴스 태그 연동 실패: {}", record.value(), e);
        }
    }
}
