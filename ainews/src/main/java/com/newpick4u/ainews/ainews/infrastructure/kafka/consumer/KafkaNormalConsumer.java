package com.newpick4u.ainews.ainews.infrastructure.kafka.consumer;

import com.newpick4u.ainews.ainews.application.usecase.AiNewsService;
import com.newpick4u.ainews.ainews.infrastructure.kafka.producer.KafkaAiProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaNormalConsumer {

  private final AiNewsService aiNewsService;
  private final KafkaAiProducer kafkaAiProducer;

  @Value("${app.kafka.consumer.normal.enable-spring-ackmode-immediate:false}")
  private boolean NORMAL_ACKMODE_IMMEDIATE;

  @Value("${app.kafka.consumer.normal.enable-auto-commit:true}")
  private boolean NORMAL_ENABLE_AUTO_COMMIT;

  /**
   * 원본 뉴스 수신
   */
  @KafkaListener(
      topics = "${app.kafka.consumer.normal.topic.originnews-info.topic-name}",
      groupId = "${app.kafka.consumer.normal.topic.originnews-info.group-id}",
      containerFactory = "normalKafkaListenerContainerFactory"
  )
  public void listenOriginNews(ConsumerRecord<String, String> record, Acknowledgment ack) {
    String message = null;
    try {
      message = record.value();
      aiNewsService.processAiNews(message);

      // 수동 커밋 모드에서만 실행
      if (NORMAL_ACKMODE_IMMEDIATE
          && !NORMAL_ENABLE_AUTO_COMMIT) {
        ack.acknowledge();
      }
    } catch (Exception e) {
      log.error("Consume Fail -> Retry : {}", record.value(), e);
    }
  }
}
