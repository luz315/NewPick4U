package com.newpick4u.user.infrastructure.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.newpick4u.user.application.message.producer.PointRequestFailureProducer;
import com.newpick4u.user.application.message.request.PointRequestFailureMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PointRequestFailureProducerImpl implements PointRequestFailureProducer {

  private final KafkaTemplate<String, String> kafkaTemplate;
  private final ObjectMapper objectMapper;
  @Value("${spring.kafka.producer.topics.point-request-failure}")
  private String topic;

  @Override
  public void produce(PointRequestFailureMessage message) {
    try {
      String parsedMessage = parsedMessage(message);
      kafkaTemplate.send(topic, parsedMessage);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private String parsedMessage(PointRequestFailureMessage message) throws JsonProcessingException {
    return objectMapper.writeValueAsString(message);
  }
}
