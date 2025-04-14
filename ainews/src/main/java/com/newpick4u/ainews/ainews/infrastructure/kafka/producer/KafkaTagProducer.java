package com.newpick4u.ainews.ainews.infrastructure.kafka.producer;

import com.newpick4u.ainews.ainews.application.TagQueueClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KafkaTagProducer implements TagQueueClient {

  private final KafkaTemplate<String, String> normalKafkaTemplate;
  private final KafkaTemplate<String, String> exceptionKafkaTemplate;

  @Value("${app.kafka.producer.normal.topic.tag-info.topic-name}")
  private String tagTopicName;

  @Value("${app.kafka.producer.exceptional.topic.tag-info-dlq.topic-name}")
  private String tagExceptionTopicName;


  public KafkaTagProducer(
      @Qualifier("normalKafkaTemplate")
      KafkaTemplate<String, String> normalKafkaTemplate,
      @Qualifier("exceptionKafkaTemplate")
      KafkaTemplate<String, String> exceptionKafkaTemplate) {
    this.normalKafkaTemplate = normalKafkaTemplate;
    this.exceptionKafkaTemplate = exceptionKafkaTemplate;
  }

  // 정상 케이스 전송
  @Override
  public void sendTag(String message) {
    try {
      normalKafkaTemplate.send(tagTopicName, message)
          .thenAccept(result -> {
          }).exceptionally(ex -> {
            log.error("Fail Send Message [{}] : {}", tagTopicName, message, ex);
            sendTagDLQ(message);
            return null;
          });

    } catch (Exception e) {
      log.error("Fail Send Message [{}] : {}", tagTopicName, message, e);
      sendTagDLQ(message);
    }
  }

  // 실패 케이스 전송
  @Override
  public void sendTagDLQ(String message) {
    try {
      exceptionKafkaTemplate.send(tagExceptionTopicName, message)
          .thenAccept(result -> {
          }).exceptionally(ex -> {
            // DLQ 전송 실패는 로그만 남긴다.
            log.error("Fail Send DLQ Message [{}] : message={}", tagExceptionTopicName, message, ex);
            return null;
          });

    } catch (Exception e) {
      // DLQ 전송 실패는 로그만 남긴다.
      log.error("Fail Send DLQ Message [{}] : message={}", tagExceptionTopicName, message, e);
    }
  }
}
