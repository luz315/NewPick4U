package com.newpick4u.tag.infrastructure.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class TagDlqConsumer {

  private final KafkaTemplate<String, String> kafkaTemplate;

  @Value("${spring.kafka.consumer.topic.create}")
  private String mainTopic;

  @KafkaListener(
      topics = "${spring.kafka.dlq-topic.topic-name}",
      groupId = "tag-dlq-consumer"
  )
  public void handleTagDlq(ConsumerRecord<String, String> record, Acknowledgment ack) {

    try {
      kafkaTemplate.send(mainTopic, record.key(), record.value());
      ack.acknowledge();
      // 모니터링, 백오피스 알림(슬랙, 이메일)
    } catch (Exception e) {
      log.error("[DLQ] 처리 실패한 메세지 수신 : {}", record.value());
    }
  }
}
