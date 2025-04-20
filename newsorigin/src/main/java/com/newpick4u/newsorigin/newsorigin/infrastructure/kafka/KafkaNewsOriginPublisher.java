package com.newpick4u.newsorigin.newsorigin.infrastructure.kafka;

import com.newpick4u.newsorigin.newsorigin.application.EventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaNewsOriginPublisher implements EventPublisher {

  private final KafkaTemplate<String, String> kafkaTemplate;

  @Value("${app.kafka.producer.topic.news-origin.topic-name}")
  private String NEWS_ORIGIN_TOPIC_NAME;

  /**
   * 뉴스 원본 정보를 전송
   *
   * @param message
   * @return 메세지 전송 성공 여부
   */
  public boolean sendNewsOriginMessage(String message) {
    try {
      kafkaTemplate.send(NEWS_ORIGIN_TOPIC_NAME, message);
      return true;
    } catch (Exception e) {
      log.error("Kafka Send Fail : {}", NEWS_ORIGIN_TOPIC_NAME, e);
      return false;
    }
  }
}
