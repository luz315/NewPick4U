package com.newpick4u.comment.comment.infrastructure.kafka;

import com.newpick4u.comment.comment.application.EventPublisher;
import com.newpick4u.comment.comment.application.EventType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KafkaAdvertisementProducer implements EventPublisher {

  private final KafkaTemplate<String, String> normalKafkaTemplate;
  private final KafkaTemplate<String, String> exceptionKafkaTemplate;

  @Value("${app.kafka.producer.normal.topic.point-request.topic-name}")
  private String pointRequestTopicName;

  @Value("${app.kafka.producer.exceptional.topic.point-request-dlq.topic-name}")
  private String pointRequestExceptionTopicName;

  public KafkaAdvertisementProducer(
      @Qualifier("normalKafkaTemplate")
      KafkaTemplate<String, String> normalKafkaTemplate,
      @Qualifier("exceptionKafkaTemplate")
      KafkaTemplate<String, String> exceptionKafkaTemplate) {
    this.normalKafkaTemplate = normalKafkaTemplate;
    this.exceptionKafkaTemplate = exceptionKafkaTemplate;
  }

  @Override
  public boolean isSupport(EventType eventType) {
    switch (eventType) {
      case POINT_REQUEST_SEND, FAIL_POINT_REQUEST -> {
        return true;
      }
      default -> {
        return false;
      }
    }
  }

  @Override
  public void sendMessage(String message, EventType eventType) {
    switch (eventType) {
      case POINT_REQUEST_SEND -> {
        sendPointRequestMessage(message);
      }
      case FAIL_POINT_REQUEST -> {
        sendPointRequestDLQ(message);
      }
    }
  }

  // 정상 케이스 전송
  private void sendPointRequestMessage(String message) {
    try {
      normalKafkaTemplate.send(pointRequestTopicName, message)
          .thenAccept(result -> {
          }).exceptionally(ex -> {
            log.error("Fail Send Message [{}] : {}", pointRequestTopicName, message, ex);
            throw new RuntimeException(ex);
          });

    } catch (Exception e) {
      log.error("Finally Fail Send Message [{}] : {}", pointRequestTopicName, message, e);
      throw new RuntimeException(e);
    }
  }

  // 실패 케이스 전송
  private void sendPointRequestDLQ(String message) {
    try {
      exceptionKafkaTemplate.send(pointRequestExceptionTopicName, message)
          .thenAccept(result -> {
          }).exceptionally(ex -> {
            // DLQ 전송 실패는 로그만 남긴다.
            log.error("Fail Send DLQ Message [{}] : message={}", pointRequestExceptionTopicName,
                message, ex);
            return null;
          });

    } catch (Exception e) {
      // DLQ 전송 실패는 로그만 남긴다.
      log.error("Fail Send DLQ Message [{}] : message={}", pointRequestExceptionTopicName, message,
          e);
    }
  }
}
