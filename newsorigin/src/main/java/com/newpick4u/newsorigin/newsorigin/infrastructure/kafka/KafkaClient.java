package com.newpick4u.newsorigin.newsorigin.infrastructure.kafka;

import com.newpick4u.newsorigin.newsorigin.application.MessageClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class KafkaClient implements MessageClient {

  private final KafkaTemplate<String, String> kafkaTemplate;

  @Value("${spring.kafka.newsorigin-topic.topic-name}")
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
      return false;
    }
  }
}
