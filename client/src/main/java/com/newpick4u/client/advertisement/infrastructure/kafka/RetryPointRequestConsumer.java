package com.newpick4u.client.advertisement.infrastructure.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.newpick4u.client.advertisement.application.message.request.PointRequestMessage;
import com.newpick4u.client.advertisement.application.usecase.AdvertisementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RetryPointRequestConsumer {
  
  private final ObjectMapper objectMapper;
  private final AdvertisementService advertisementService;

  @KafkaListener(
      topics = "${spring.kafka.consumer.topics.point-request-dlq}",
      groupId = "${spring.kafka.consumer.groups.point-request-dlq}"
  )
  public void consume(ConsumerRecord<String, String> record, Acknowledgment acknowledgment) {
    try {
      PointRequestMessage message = objectMapper.readValue(record.value(),
          PointRequestMessage.class);
      log.info("[DLQ Retry] 포인트 DLQ 재처리 시작:{}", message);
      advertisementService.updatePointGrantedCount(message);
      acknowledgment.acknowledge();
    } catch (Exception e) {
      log.error("[DLQ Retry] 최대 재시도 횟수 초과로 인한 메시지 폐기");
      acknowledgment.acknowledge();
      return;
    }
  }
}


