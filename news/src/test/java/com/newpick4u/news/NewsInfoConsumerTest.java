package com.newpick4u.news;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.newpick4u.news.news.application.dto.NewsInfoDto;
import com.newpick4u.news.news.domain.entity.News;
import com.newpick4u.news.news.domain.entity.NewsStatus;
import com.newpick4u.news.news.domain.repository.NewsRepository;
import com.newpick4u.news.news.infrastructure.kafka.KafkaConfig;
import com.newpick4u.news.news.infrastructure.kafka.NewsInfoConsumer;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;



@SpringBootTest(properties = {
        "eureka.client.enabled=false"
})
@ActiveProfiles("test")
@EmbeddedKafka(partitions = 1, topics = {"news-info.fct.v1", "news-info-dlq.fct.v1"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) // 이게 있으면 상태가 초기화됩니다
@Import({KafkaConfig.class, NewsInfoConsumer.class})
class NewsInfoConsumerTest {

    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Test
    void 뉴스_초안_정상_컨슈밍_처리() throws Exception {
        // given

        Thread.sleep(1000);

        String aiNewsId = "ai-normal-001";
        NewsInfoDto dto = new NewsInfoDto(aiNewsId, "정상 제목", "정상 내용", "https://test.com", "2025-04-13");
        String json = objectMapper.writeValueAsString(dto);

        // when
        kafkaTemplate.send(new ProducerRecord<>("news-info.fct.v1", aiNewsId, json));

        // then
        Awaitility.await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            Optional<News> saved = newsRepository.findByAiNewsId(aiNewsId);
            assertThat(saved).isPresent();
            assertThat(saved.get().getTitle()).isEqualTo("정상 제목");
            assertThat(saved.get().getStatus()).isEqualTo(NewsStatus.PENDING);
        });
    }
    @Test
    void 뉴스_초안_DLQ_리트라이_1회성공() throws Exception {
        // given
        String aiNewsId = "fail-once";
        NewsInfoDto dto = new NewsInfoDto(aiNewsId, "재시도 제목", "재시도 내용", "http://url.com", "2025-04-13");
        String json = objectMapper.writeValueAsString(dto);

        // when
        kafkaTemplate.send(new ProducerRecord<>("news-info.fct.v1", aiNewsId, json));

        // then
        Awaitility.await()
                .pollDelay(Duration.ofSeconds(1)) // 초기 등록 대기
                .pollInterval(Duration.ofMillis(500)) // 0.5초마다 체크
                .atMost(Duration.ofSeconds(10))
                .untilAsserted(() -> {
                    Optional<News> newsOpt = newsRepository.findByAiNewsId(aiNewsId);
                    assertThat(newsOpt).isPresent(); // 리트라이 이후 저장 성공
                    assertThat(newsOpt.get().getTitle()).isEqualTo("재시도 제목");
                });
    }

    @Test
    void 뉴스_초안_DLQ_3회실패_최종실패_및_DLQ확인() throws Exception {
        // given
        String aiNewsId = "fail-me";
        NewsInfoDto dto = new NewsInfoDto(aiNewsId, "DLQ 제목", "DLQ 내용", "http://url.com", "2025-04-13");
        String json = objectMapper.writeValueAsString(dto);

        // when
        kafkaTemplate.send(new ProducerRecord<>("news-info.fct.v1", aiNewsId, json));

        // then - DLQ로 넘어간 메시지가 있는지 확인
        Awaitility.await()
                .pollDelay(Duration.ofSeconds(2)) // DLQ publish 시간 확보
                .pollInterval(Duration.ofSeconds(1)) // 1초마다 확인
                .atMost(Duration.ofSeconds(20))
                .untilAsserted(() -> {
                    Map<String, Object> props = KafkaTestUtils.consumerProps("dlq-test-group", "true", embeddedKafkaBroker);
                    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

                    try (Consumer<String, String> consumer = new DefaultKafkaConsumerFactory<>(
                            props, new StringDeserializer(), new StringDeserializer()).createConsumer()) {

                        consumer.subscribe(List.of("news-info-dlq.fct.v1"));

                        ConsumerRecords<String, String> records = KafkaTestUtils.getRecords(consumer, Duration.ofSeconds(2));

                        boolean foundInDlq = StreamSupport.stream(records.spliterator(), false)
                                .anyMatch(record -> "fail-me".equals(record.key()));

                        assertThat(foundInDlq).isTrue();
                    }
                });
    }
}
