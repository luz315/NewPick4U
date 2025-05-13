//package com.newpick4u.news.news.infrastructure.kafka;
//
//import com.newpick4u.news.news.application.dto.NewsTagDto;
//import com.newpick4u.news.news.application.usecase.NewsService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.kafka.clients.consumer.ConsumerRecord;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.kafka.support.Acknowledgment;
//import org.springframework.stereotype.Component;
//
//import java.util.Optional;
//
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class RetryNewsTagConsumer {
//
//    private final NewsService newsService;
//
//    @KafkaListener(
//            topics = "tag-dlq.fct.v1",
//            groupId = "news-tag-dlq-consumer",
//            containerFactory = "newsTagListenerContainerFactory"
//    )
//    public void consume(ConsumerRecord<String, NewsTagDto> record, Acknowledgment ack) {
//        NewsTagDto dto = record.value();
//        int attempt = getRetryAttempt(record);
//        try {
//            log.info("[DLQ Retry] 태그 DLQ 재처리 시작: {}", dto);
//            newsService.updateNewsTagList(dto);
//            ack.acknowledge();
//        } catch (Exception e) {
//            log.error("[DLQ Retry] 태그 재처리 실패 - 메시지: {}", dto, e);
//            if (attempt >= 3) {
//                log.error("[DLQ Retry] 최대 재시도 초과. 메시지 폐기: {}", dto);
//                ack.acknowledge(); // 커밋하고 종료 (폐기)
//            }
//        }
//    }
//
//    private int getRetryAttempt(ConsumerRecord<?, ?> record) {
//        return Optional.ofNullable(record.headers().lastHeader("kafka_NewsAttempt"))
//                .map(header -> header.value()[0]) // byte에서 int 변환
//                .map(value -> (int) value)
//                .orElse(1);
//    }
//}