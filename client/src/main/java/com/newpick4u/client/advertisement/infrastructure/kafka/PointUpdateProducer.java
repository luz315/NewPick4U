package com.newpick4u.client.advertisement.infrastructure.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.newpick4u.client.advertisement.application.message.request.PointUpdateMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PointUpdateProducer {

  private final KafkaTemplate<String, String> kafkaTemplate;
  private final ObjectMapper objectMapper;
  @Value("${spring.kafka.producer.topics.point-update}")
  private String topic;

  @EventListener
  public void produce(PointUpdateMessage message) {
    try {
      String parsedMessage = serializeMessage(message);
      kafkaTemplate.send(topic, parsedMessage);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private String serializeMessage(PointUpdateMessage message) throws JsonProcessingException {
    return objectMapper.writeValueAsString(message);
  }
}
