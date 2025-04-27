package com.newpick4u.news.news.infrastructure.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.newpick4u.news.news.application.dto.NewsInfoDto;
import com.newpick4u.news.news.application.dto.NewsTagDto;
import com.newpick4u.news.news.application.usecase.NewsService;
import com.newpick4u.news.news.domain.repository.TagInboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NewsInfoConsumer {

  private final NewsService newsService;
  private final TagInboxRepository tagInboxRepository;
  private final ObjectMapper objectMapper;

  @KafkaListener(
      topics = "news-info.fct.v1",
      groupId = "news-info-consumer",
      containerFactory = "newsInfoListenerContainerFactory"
  )
  public void consume(ConsumerRecord<String, NewsInfoDto> record, Acknowledgment ack) {
    try {
      log.info("Consumed Kafka Message: {}", record.value());
      newsService.saveNewsInfo(record.value());
      processTagInbox(record.value().aiNewsId());
      ack.acknowledge();
    } catch (Exception e) {
//            ack.nack(Duration.ofSeconds(3));
      log.error("[Kafka] 뉴스 저장 실패 - 메시지: {}", record.value(), e);
      throw e; // 이걸 넣자!
    }
  }

  private void processTagInbox(String aiNewsId) {
    // 3. TagInbox에서 해당 뉴스 ID에 매핑된 태그 정보를 가져오기
    tagInboxRepository.findByAiNewsId(aiNewsId).ifPresent(inbox -> {
      try {
        // TagInbox에서 태그 리스트를 가져와 뉴스에 적용
        newsService.updateNewsTagList(objectMapper.readValue(inbox.getJsonPayload(), NewsTagDto.class));
        // TagInbox 처리 후 삭제
        tagInboxRepository.delete(inbox);
        log.info("[TagInbox] 태그 적용 완료 및 삭제: aiNewsId={}", aiNewsId);
      } catch (Exception e) {
        log.warn("[TagInbox] 처리 실패: aiNewsId={}", aiNewsId, e);
      }
    });
  }
}