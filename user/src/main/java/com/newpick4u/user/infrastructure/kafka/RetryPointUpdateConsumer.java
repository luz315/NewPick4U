package com.newpick4u.user.infrastructure.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.newpick4u.user.application.dto.request.PointUpdateMessage;
import com.newpick4u.user.application.message.producer.PointRequestFailureProducer;
import com.newpick4u.user.application.message.request.PointRequestFailureMessage;
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
  private final PointRequestFailureProducer pointRequestFailureProducer;

  @KafkaListener(topics = "${spring.kafka.consumer.topics.point-update-dlq}",
      groupId = "${spring.kafka.consumer.groups.user-point-update-dlq}")
  public void consume(ConsumerRecord<String, String> record, Acknowledgment ack) {
    try {
      PointUpdateMessage request = objectMapper.readValue(record.value(),
          PointUpdateMessage.class);
      PointRequestFailureMessage message = PointRequestFailureMessage.of(request.userId(),
          request.advertisementId());

      pointRequestFailureProducer.produce(message);
      userService.updatePoint(request);
      ack.acknowledge();
    } catch (Exception e) {
      log.error("포인트 업데이트 취소 이벤트 발행 실패", e);
    } finally {
      ack.acknowledge();
    }
  }
}
