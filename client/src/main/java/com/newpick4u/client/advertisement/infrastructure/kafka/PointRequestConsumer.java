package com.newpick4u.client.advertisement.infrastructure.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.newpick4u.client.advertisement.application.exception.AdvertisementException.NotFoundException;
import com.newpick4u.client.advertisement.application.message.request.PointRequestMessage;
import com.newpick4u.client.advertisement.application.usecase.AdvertisementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PointRequestConsumer {

  private final ObjectMapper objectMapper;
  private final AdvertisementService advertisementService;

  private void verifyNotFoundException(Exception exception, Acknowledgment acknowledgment)
      throws Exception {
    if (exception instanceof NotFoundException) {
      acknowledgment.acknowledge(); // 존재하지 않는 광고의 경우 재시도 하지 못하게 함
      return;
    }
    throw new RuntimeException();
  }

  @KafkaListener(topics = "${spring.kafka.consumer.topics.point-request}",
      groupId = "${spring.kafka.consumer.groups.point-request}",
      containerFactory = "updateMessageConcurrentKafkaListenerContainerFactory"
  )
  public void consume(ConsumerRecord<String, String> record, Acknowledgment ack)
      throws Exception {
    log.info("Kafka Raw Value: {}", record.value());
    try {
      PointRequestMessage message = objectMapper.readValue(record.value(),
          PointRequestMessage.class);
      advertisementService.updatePointGrantedCount(message);
      ack.acknowledge();
    } catch (Exception e) {
      log.error("Kafka consume error", e);
      verifyNotFoundException(e, ack);
    }
  }
}

