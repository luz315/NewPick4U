package com.newpick4u.comment.comment.infrastructure.kafka;

import com.newpick4u.comment.comment.application.EventType;
import com.newpick4u.comment.comment.application.MessagePublishService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaExceptionConsumer {

  private final MessagePublishService messagePublishService;

  @KafkaListener(
      topics = "${app.kafka.consumer.exception.topic.point-request-dlq.topic-name}",
      groupId = "${app.kafka.consumer.exception.topic.point-request-dlq.group-id}",
      containerFactory = "exceptionKafkaListenerContainerFactory"
  )
  public void listenPointRequestFailDLQ(ConsumerRecord<String, String> record, Acknowledgment ack) {
    String message = null;
    try {
      message = record.value();
      messagePublishService.sendMessage(message, EventType.POINT_REQUEST_SEND);

      ack.acknowledge();

    } catch (Exception e) {
      log.error("SaveFailDLQ Consume Fail : -> Retry {}", message, e);
    }
  }
}
