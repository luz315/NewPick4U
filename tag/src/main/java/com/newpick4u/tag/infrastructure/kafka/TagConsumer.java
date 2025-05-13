package com.newpick4u.tag.infrastructure.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.newpick4u.tag.application.dto.AiNewsDto;
import com.newpick4u.tag.application.dto.NewsTagDto;
import com.newpick4u.tag.application.dto.NewsTagDto.TagDto;
import com.newpick4u.tag.application.usecase.TagMessageHandler;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TagConsumer {

  private final TagMessageHandler tagMessageHandler;
  private final KafkaTemplate<String, String> kafkaTemplate;
  private final ObjectMapper objectMapper;

  @Value("${spring.kafka.tag-topic.topic-name}")
  private String tagTopicName;

  @KafkaListener(
      topics = "${spring.kafka.consumer.topic.create}",
      groupId = "${spring.kafka.consumer.create.group-id}",
      containerFactory = "kafkaListenerContainerFactory"
  )
  public void consume(ConsumerRecord<String, String> record, Acknowledgment ack)
      throws JsonProcessingException {
    AiNewsDto dto = objectMapper.readValue(record.value(), AiNewsDto.class);
    log.info("dto.tagList().get(0) : {}", dto.tags().get(0));
    try {

      List<TagDto> tagList = tagMessageHandler.handle(dto);

      NewsTagDto newsTagDto = new NewsTagDto(String.valueOf(dto.aiNewsId()), tagList);
      String sendMessage = objectMapper.writeValueAsString(newsTagDto);
      kafkaTemplate.send(tagTopicName, sendMessage);

      log.info("Kafka 메시지 전송 완료: dto={}", sendMessage);

      ack.acknowledge();

    } catch (Exception e) {
      log.error("[Kafka] 태그 저장 실패 : {}", dto, e);
    }
  }


  @KafkaListener(
      topics = "${spring.kafka.consumer.topic.delete}",
      groupId = "${spring.kafka.consumer.delete.group-id}",
      containerFactory = "kafkaListenerContainerFactory"
  )
  public void deleteTag(ConsumerRecord<String, String> record, Acknowledgment ack)
      throws JsonProcessingException {
    AiNewsDto dto = objectMapper.readValue(record.value(), AiNewsDto.class);
    try {
      tagMessageHandler.deleteTagFromAi(dto);
      ack.acknowledge();
    } catch (Exception e) {
      log.error("[kafka] 태그 삭제 실패 : {}", dto, e);
    }
  }
}
