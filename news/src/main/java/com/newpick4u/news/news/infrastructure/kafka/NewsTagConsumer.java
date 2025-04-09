package com.newpick4u.news.news.infrastructure.kafka;

import com.newpick4u.news.news.application.dto.NewsTagDto;
import com.newpick4u.news.news.application.usecase.NewsService;
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

    private final NewsService newsService;

    @KafkaListener(
            topics = "tag.fct.v1",
            groupId = "news-tag-consumer",
            containerFactory = "newsTagListenerContainerFactory"
    )
    public void consume(ConsumerRecord<String, NewsTagDto> record, Acknowledgment ack) {
        try {
            newsService.updateNewsTagList(record.value());
            ack.acknowledge();
        } catch (Exception e) {
            log.error("[Kafka] 뉴스 태그 연동 실패: {}", record.value(), e);
            throw new RuntimeException(e);
        }
    }
}
