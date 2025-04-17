package com.newpick4u.client.advertisement.infrastructure.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.newpick4u.client.advertisement.application.message.request.PointRequestFailureMessage;
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
public class PointRequestFailureConsumer {

  private final ObjectMapper objectMapper;
  private final AdvertisementService advertisementService;

  @KafkaListener(topics = "${spring.kafka.consumer.topics.point-request-failure}",
      groupId = "${spring.kafka.consumer.groups.point-request-failure}",
      containerFactory = "pointRequestFailureMessageConcurrentKafkaListenerContainerFactory")
  public void consume(ConsumerRecord<String, String> record, Acknowledgment ack)
      throws JsonProcessingException {
    try {
      PointRequestFailureMessage message = objectMapper.readValue(record.value(),
          PointRequestFailureMessage.class);
      advertisementService.cancelPointRequest(message);
      ack.acknowledge();
    } catch (Exception e) {
      log.error("포인트 요청 취소 처리 중 예외 발생 = {}", e.getMessage(), e);
      throw e;
    }
  }

}
