package com.newpick4u.client.advertisement.infrastructure.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.newpick4u.client.advertisement.application.message.request.PointRequestFailureMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RetryPointRequestFailureConsumer {

  private final ObjectMapper objectMapper;

  // TODO : 메시지 폐기와 동시에 알림 발송 고려 중
  @KafkaListener(
      topics = "${spring.kafka.consumer.topics.point-request-failure-dlq}",
      groupId = "${spring.kafka.consumer.groups.point-request-failure-dlq}"
  )
  public void consume(ConsumerRecord<String, String> record, Acknowledgment ack) {
    try {
      PointRequestFailureMessage message = objectMapper.readValue(record.value(),
          PointRequestFailureMessage.class);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    } finally {
      ack.acknowledge();
      log.info("재시도 횟수 초과로 인한 메시지 폐기 = {}", record.value());
    }
  }
}
