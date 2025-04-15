package com.newpick4u.news;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.newpick4u.news.news.application.dto.NewsInfoDto;
import com.newpick4u.news.news.application.dto.NewsTagDto;
import com.newpick4u.news.news.application.dto.NewsTagDto.TagDto;
import com.newpick4u.news.news.domain.entity.News;
import com.newpick4u.news.news.domain.entity.TagInbox;
import com.newpick4u.news.news.domain.repository.NewsRepository;
import com.newpick4u.news.news.domain.repository.TagInboxRepository;
import com.newpick4u.news.news.infrastructure.kafka.KafkaConfig;
import com.newpick4u.news.news.infrastructure.kafka.NewsInfoConsumer;
import com.newpick4u.news.news.infrastructure.kafka.NewsTagConsumer;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(properties = {
        "eureka.client.enabled=false"
})
@ActiveProfiles("test")
@EmbeddedKafka(partitions = 1, topics = {"tag.fct.v1", "tag-dlq.fct.v1"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) // 이게 있으면 상태가 초기화됩니다
@Import({KafkaConfig.class, NewsTagConsumer.class})
class NewsTagConsumerTest {

    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    private TagInboxRepository tagInboxRepository;

    @Test
    void 뉴스_태그_정상_컨슈밍_및_뉴스_연결() throws Exception {
        // given
        String aiNewsId = "news-tag-001";
        News news = News.create(aiNewsId, "태그 대상", "내용", "https://example.com", "2025-04-13", 0L);
        newsRepository.save(news);
        newsRepository.flush();

        NewsTagDto dto = new NewsTagDto(aiNewsId, List.of(
                new TagDto(UUID.fromString("00000000-0000-0000-0000-000000000001"), "정치"), new TagDto(UUID.fromString("00000000-0000-0000-0000-000000000002"), "경제"))
        );
        String json = objectMapper.writeValueAsString(dto);

        // when
        kafkaTemplate.send(new ProducerRecord<>("tag.fct.v1", aiNewsId, json));

        // then
        Awaitility.await().untilAsserted(() -> {
            assertThat(newsRepository.findByAiNewsId(dto.aiNewsId())).isPresent();
        });
    }

    @Test
    void 뉴스태그_1회_실패후_재시도_성공() throws Exception {
        // given
        String aiNewsId = "fail-once";
        NewsTagDto dto = new NewsTagDto(aiNewsId, createSampleTags());
        String json = objectMapper.writeValueAsString(dto);

        // when
        kafkaTemplate.send("tag.fct.v1", aiNewsId, json);

        // then
        Awaitility.await().atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    assertThat(tagInboxRepository.findAll()).isEmpty(); // 정상 처리되었기 때문에 인박스 없음
                });
    }

    @Test
    void 뉴스태그_지속적_실패시_DLQ전송확인() throws Exception {
        // given
        String aiNewsId = "fail-me";
        NewsTagDto dto = new NewsTagDto(aiNewsId, createSampleTags());
        String json = objectMapper.writeValueAsString(dto);

        // when
        kafkaTemplate.send("tag.fct.v1", aiNewsId, json);

        // then - DLQ에 "fail-me"가 포함된 메시지가 들어갔는지 확인
        Awaitility.await()
                .pollDelay(Duration.ofSeconds(2))
                .pollInterval(Duration.ofSeconds(1))
                .atMost(Duration.ofSeconds(20))
                .untilAsserted(() -> {
                    Map<String, Object> props = KafkaTestUtils.consumerProps("tag-dlq-test-group", "true", embeddedKafkaBroker);
                    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

                    try (Consumer<String, String> consumer = new DefaultKafkaConsumerFactory<>(
                            props, new StringDeserializer(), new StringDeserializer()).createConsumer()) {

                        consumer.subscribe(List.of("tag-dlq.fct.v1"));

                        ConsumerRecords<String, String> records = KafkaTestUtils.getRecords(consumer, Duration.ofSeconds(2));

                        boolean foundInDlq = StreamSupport.stream(records.spliterator(), false)
                                .anyMatch(record -> record.value().contains("fail-me"));

                        assertThat(foundInDlq).isTrue();
                    }
                });
    }

    @Test
    void 뉴스없을때_태그인박스에_저장됨() throws Exception {
        // given
        String aiNewsId = "ai-no-news-" + UUID.randomUUID(); // 중복 방지
        NewsTagDto dto = new NewsTagDto(aiNewsId, createSampleTags());
        String json = objectMapper.writeValueAsString(dto);

        // when
        kafkaTemplate.send("tag.fct.v1", aiNewsId, json);

        // then
        Awaitility.await()
                .pollDelay(Duration.ofSeconds(1))
                .pollInterval(Duration.ofMillis(500))
                .atMost(Duration.ofSeconds(10))
                .untilAsserted(() -> {
                    List<TagInbox> result = tagInboxRepository.findAll();
                    assertThat(result)
                            .anyMatch(inbox -> inbox.getAiNewsId().equals(aiNewsId));
                });
    }

    private List<NewsTagDto.TagDto> createSampleTags() {
        return List.of(
                new NewsTagDto.TagDto(UUID.randomUUID(), "정치"),
                new NewsTagDto.TagDto(UUID.randomUUID(), "경제"),
                new NewsTagDto.TagDto(UUID.randomUUID(), "사회"),
                new NewsTagDto.TagDto(UUID.randomUUID(), "문화"),
                new NewsTagDto.TagDto(UUID.randomUUID(), "기술"),
                new NewsTagDto.TagDto(UUID.randomUUID(), "건강"),
                new NewsTagDto.TagDto(UUID.randomUUID(), "과학")
        );
    }
}