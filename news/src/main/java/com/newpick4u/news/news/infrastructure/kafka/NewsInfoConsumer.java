package com.newpick4u.news.news.infrastructure.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.newpick4u.news.global.exception.NewsErrorCode;
import com.newpick4u.news.news.application.dto.NewsInfoDto;
import com.newpick4u.news.news.application.dto.NewsTagDto;
import com.newpick4u.news.news.application.usecase.NewsService;
import com.newpick4u.news.news.domain.repository.TagInboxRepository;
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

  private final NewsService newsService;
  private final TagInboxRepository tagInboxRepository;
  private final ObjectMapper objectMapper;

  @KafkaListener(
      topics = "news-info.fct.v1",
      groupId = "news-info-consumer",
      containerFactory = "newsInfoListenerContainerFactory"
  )
  public void consume(ConsumerRecord<String, NewsInfoDto> record, Acknowledgment ack) {
    try {
      log.info("Consumed Kafka Message: {}", record.value());
      newsService.saveNewsInfoAndUpdateTags(record.value());
      ack.acknowledge();
    } catch (Exception e) {
//            ack.nack(Duration.ofSeconds(3));
      log.error("[Kafka] 뉴스 저장 실패 - 메시지: {}", record.value(), e);
      throw CustomException.from(NewsErrorCode.KAFKA_NEWS_SAVE_FAIL);
    }
  }
}