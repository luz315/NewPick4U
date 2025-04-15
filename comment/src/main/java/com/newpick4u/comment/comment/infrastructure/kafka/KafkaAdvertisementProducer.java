package com.newpick4u.comment.comment.infrastructure.kafka;

import com.newpick4u.comment.comment.application.AdvertisementMessageClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KafkaAdvertisementProducer implements AdvertisementMessageClient {

  private final KafkaTemplate<String, String> normalKafkaTemplate;
  private final KafkaTemplate<String, String> exceptionKafkaTemplate;

  @Value("${app.kafka.producer.normal.topic.point-request.topic-name}")
  private String newsTopicName;

  @Value("${app.kafka.producer.exceptional.topic.point-request-dlq.topic-name}")
  private String newsExceptionTopicName;

  public KafkaAdvertisementProducer(
      @Qualifier("normalKafkaTemplate")
      KafkaTemplate<String, String> normalKafkaTemplate,
      @Qualifier("exceptionKafkaTemplate")
      KafkaTemplate<String, String> exceptionKafkaTemplate) {
    this.normalKafkaTemplate = normalKafkaTemplate;
    this.exceptionKafkaTemplate = exceptionKafkaTemplate;
  }

  // 정상 케이스 전송
  @Override
  public void sendPointRequestMessage(String message) {
    try {
      normalKafkaTemplate.send(newsTopicName, message)
          .thenAccept(result -> {
          }).exceptionally(ex -> {
            log.error("Fail Send Message [{}] : {}", newsTopicName, message, ex);
            sendPointRequestDLQ(message);
            return null;
          });

    } catch (Exception e) {
      log.error("Fail Send Message [{}] : {}", newsTopicName, message, e);
      sendPointRequestDLQ(message);
    }
  }

  // 실패 케이스 전송
  @Override
  public void sendPointRequestDLQ(String message) {
    try {
      exceptionKafkaTemplate.send(newsExceptionTopicName, message)
          .thenAccept(result -> {
          }).exceptionally(ex -> {
            // DLQ 전송 실패는 로그만 남긴다.
            log.error("Fail Send DLQ Message [{}] : message={}", newsExceptionTopicName, message, ex);
            return null;
          });

    } catch (Exception e) {
      // DLQ 전송 실패는 로그만 남긴다.
      log.error("Fail Send DLQ Message [{}] : message={}", newsExceptionTopicName, message, e);
    }
  }
}
