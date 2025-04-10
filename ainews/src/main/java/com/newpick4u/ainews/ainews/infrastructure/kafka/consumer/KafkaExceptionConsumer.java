package com.newpick4u.ainews.ainews.infrastructure.kafka.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.newpick4u.ainews.ainews.application.NewsQueueClient;
import com.newpick4u.ainews.ainews.application.TagQueueClient;
import com.newpick4u.ainews.ainews.application.usecase.AiNewsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaExceptionConsumer {

  private final AiNewsService aiNewsService;
  private final TagQueueClient tagQueueClient;
  private final NewsQueueClient newsQueueClient;

  @Value("${app.kafka.consumer.exception.enable-spring-ackmode-immediate:false}")
  private boolean EXCEPTION_ACKMODE_IMMEDIATE;

  @Value("${app.kafka.consumer.exception.enable-auto-commit:true}")
  private boolean EXCEPTION_ENABLE_AUTO_COMMIT;

  /**
   * 원본 뉴스 재처리 수신
   */
  @KafkaListener(
      topics = "${app.kafka.consumer.exception.topic.originnews-info-dlq.topic-name}",
      groupId = "${app.kafka.consumer.exception.topic.originnews-info-dlq.group-id}",
      containerFactory = "exceptionKafkaListenerContainerFactory"
  )
  public void listenOriginNewsDLQ(ConsumerRecord<String, String> record, Acknowledgment ack) {
    String message = null;
    try {
      message = record.value();
      aiNewsService.processAiNews(message);

      // 수동 커밋 모드에서만 실행
      if (EXCEPTION_ACKMODE_IMMEDIATE
          && !EXCEPTION_ENABLE_AUTO_COMMIT) {
        ack.acknowledge();
      }
    } catch (Exception e) {
      log.error("OriginNewsDLQ Consume Fail : -> Retry {}", message, e);
    }
  }

  /**
   * AI 뉴스 저장 실패 재처리 수신
   */
  @KafkaListener(
      topics = "${app.kafka.consumer.exception.topic.ainews-dlq.topic-name}",
      groupId = "${app.kafka.consumer.exception.topic.ainews-dlq.group-id}",
      containerFactory = "exceptionKafkaListenerContainerFactory"
  )
  public void listenSaveFailDLQ(ConsumerRecord<String, String> record, Acknowledgment ack) {
    String message = null;
    try {
      message = record.value();
      aiNewsService.saveAndSendTaskByListener(message);

      // 수동 커밋 모드에서만 실행
      if (EXCEPTION_ACKMODE_IMMEDIATE
          && !EXCEPTION_ENABLE_AUTO_COMMIT) {
        ack.acknowledge();
      }
    } catch (JsonProcessingException e) {
      log.error("SaveFailDLQ Consume Fail : JSON Fail -> Drop : {}", message, e);
      ack.acknowledge(); // JSON 관련 예외는 재처리 안함

    } catch (Exception e) {
      log.error("SaveFailDLQ Consume Fail : -> Retry {}", message, e);
    }
  }

  /**
   * 뉴스 전송 실패 재처리 수신
   */
  @KafkaListener(
      topics = "${app.kafka.consumer.exception.topic.news-info-dlq.topic-name}",
      groupId = "${app.kafka.consumer.exception.topic.news-info-dlq.group-id}",
      containerFactory = "exceptionKafkaListenerContainerFactory"
  )
  public void listenSendNewsFailDLQ(ConsumerRecord<String, String> record, Acknowledgment ack) {
    String message = null;
    try {
      message = record.value();
      newsQueueClient.sendNews(message);

      // 수동 커밋 모드에서만 실행
      if (EXCEPTION_ACKMODE_IMMEDIATE
          && !EXCEPTION_ENABLE_AUTO_COMMIT) {
        ack.acknowledge();
      }
    } catch (Exception e) {
      log.error("SaveFailDLQ Consume Fail : -> Retry {}", message, e);
    }
  }

  /**
   * 태그 전송 실패 재처리 수신
   */
  @KafkaListener(
      topics = "${app.kafka.consumer.exception.topic.tag-info-dlq.topic-name}",
      groupId = "${app.kafka.consumer.exception.topic.tag-info-dlq.group-id}",
      containerFactory = "exceptionKafkaListenerContainerFactory"
  )
  public void listenSendTagFailDLQ(ConsumerRecord<String, String> record, Acknowledgment ack) {
    String message = null;
    try {
      message = record.value();
      tagQueueClient.sendTag(message);

      // 수동 커밋 모드에서만 실행
      if (EXCEPTION_ACKMODE_IMMEDIATE
          && !EXCEPTION_ENABLE_AUTO_COMMIT) {
        ack.acknowledge();
      }
    } catch (Exception e) {
      log.error("SaveFailDLQ Consume Fail : -> Retry {}", message, e);
    }
  }
}
