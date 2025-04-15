package com.newpick4u.user.infrastructure.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.newpick4u.user.application.dto.request.PointUpdateMessage;
import com.newpick4u.user.application.usecase.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RetryPointUpdateConsumer {

  private final ObjectMapper objectMapper;
  private final UserService userService;

  @KafkaListener(topics = "${spring.kafka.consumer.topics.point-update-dlq}",
      groupId = "${spring.kafka.consumer.groups.user-point-update-dlq}")
  public void consume(ConsumerRecord<String, String> record, Acknowledgment ack) {
    log.info("Kafka Raw Value: {}", record.value());
    try {
      PointUpdateMessage request = objectMapper.readValue(record.value(),
          PointUpdateMessage.class);
      log.info("[DLQ Retry] 포인트 DLQ 재처리 시작:{}", request);
      userService.updatePoint(request);
      ack.acknowledge();
    } catch (Exception e) {
      log.error("[DLQ Retry] 포인트 DLQ 재처리 실패", e);
      ack.acknowledge();
    }

  }
}
