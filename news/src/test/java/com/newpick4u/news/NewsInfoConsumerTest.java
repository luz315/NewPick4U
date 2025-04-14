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

//@SpringBootTest(properties = "spring.profiles.active=test")
@SpringBootTest(properties = {
        "eureka.client.enabled=false"
})

//@Import(NewsInfoConsumer.class)
@EmbeddedKafka(partitions = 1, topics = {"news-info.fct.v1", "news-info-dlq.fct.v1"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) // 이게 있으면 상태가 초기화됩니다
@Import({KafkaConfig.class, NewsInfoConsumer.class})
//@ActiveProfiles("test")
class NewsInfoConsumerTest {

    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    private KafkaTemplate<String, String> kafkaTemplate;

    private Producer<String, String> newsInfoProducer;
    private Consumer<String, NewsInfoDto> newsInfoConsumer;

    @BeforeEach
    void setup() {
//        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        Map<String, Object> producerProps = KafkaTestUtils.producerProps(embeddedKafkaBroker);
        newsInfoProducer = new DefaultKafkaProducerFactory<>(producerProps, new StringSerializer(), new StringSerializer()).createProducer();

        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("news-info-consumer", "false", embeddedKafkaBroker);
        consumerProps.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        consumerProps.put(JsonDeserializer.VALUE_DEFAULT_TYPE, NewsInfoDto.class.getName());
        consumerProps.put(JsonDeserializer.KEY_DEFAULT_TYPE, String.class.getName());
        consumerProps.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumerProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        consumerProps.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 20);
        consumerProps.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 15000);
        consumerProps.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, 1000);
        consumerProps.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 60000);

        newsInfoConsumer = new DefaultKafkaConsumerFactory<>(consumerProps, new StringDeserializer(), new JsonDeserializer<>(NewsInfoDto.class)).createConsumer();
        newsInfoConsumer.subscribe(Collections.singleton("news-info.fct.v1"));
    }

    @Test
    void 뉴스_초안_정상_컨슈밍_처리() throws Exception {
        // given

        Thread.sleep(1000);

        String aiNewsId = "ai-normal-001";
        NewsInfoDto dto = new NewsInfoDto(aiNewsId, "정상 제목", "정상 내용", "https://test.com", "2025-04-13");
        String json = objectMapper.writeValueAsString(dto);
        newsInfoProducer.send(new ProducerRecord<>("news-info.fct.v1", aiNewsId, json));

        // when

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
        NewsInfoDto dto = new NewsInfoDto("fail-once", "재시도 제목", "재시도 내용",  "http://url.com", "2025-04-13");
        String json = objectMapper.writeValueAsString(dto);

        // when
        newsInfoProducer.send(new ProducerRecord<>("news-info.fct.v1", "fail-once", json));

        // then
        Awaitility.await()
                .pollDelay(Duration.ofSeconds(2)) // 초기 등록 대기
                .pollInterval(Duration.ofSeconds(1)) // 1초마다 체크
                .atMost(Duration.ofSeconds(30))
                .untilAsserted(() -> {
                    Optional<News> newsOpt = newsRepository.findByAiNewsId("fail-once");
                    assertThat(newsOpt).isPresent(); // 이 시점에서 null이면 에러
                    assertThat(newsOpt.get().getTitle()).isEqualTo("재시도 제목");
                });

//        Awaitility.await().atMost(30, TimeUnit.SECONDS).untilAsserted(() -> {
//            News news = newsRepository.findByAiNewsId("fail-once").orElseThrow();
//            assertThat(news.getTitle()).isEqualTo("재시도 제목");
//            assertThat(news.getStatus()).isEqualTo(NewsStatus.PENDING);
//        });
    }
//
@Test
void 뉴스_초안_DLQ_3회실패_최종실패_및_DLQ확인() throws Exception {
    // given
    NewsInfoDto dto = new NewsInfoDto("fail-me", "DLQ 제목", "DLQ 내용",  "http://url.com", "2025-04-13");
    String json = objectMapper.writeValueAsString(dto);

    // when

    newsInfoProducer.send(new ProducerRecord<>("news-info.fct.v1", "fail-me", json));

    // then - DLQ로 넘어간 메시지가 있는지 확인
    Awaitility.await().atMost(15, TimeUnit.SECONDS).untilAsserted(() -> {
        // DLQ 메시지를 DTO 그대로 받아서 확인
        Map<String, Object> props = KafkaTestUtils.consumerProps("test-dlq-check", "true", embeddedKafkaBroker);
        Consumer<String, String> consumer = new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                new StringDeserializer()
        ).createConsumer();
        consumer.assign(List.of(new TopicPartition("news-info-dlq.fct.v1", 0))); // 명시적 할당

//        consumer.subscribe(List.of("news-info-dlq.fct.v1"));
        Thread.sleep(500);

        ConsumerRecords<String, String> records = KafkaTestUtils.getRecords(consumer, Duration.ofSeconds(2));

        for (ConsumerRecord<String, String> record : records) {
            System.out.println("DLQ record key: " + record.key());
            System.out.println("DLQ record value: " + record.value());
        }

        boolean foundInDlq = StreamSupport.stream(records.spliterator(), false)
                .anyMatch(record -> "fail-me".equals(record.key()));
        assertThat(foundInDlq).isTrue();
    });
}
}
