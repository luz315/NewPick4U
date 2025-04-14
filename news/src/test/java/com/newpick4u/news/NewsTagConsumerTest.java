package com.newpick4u.news;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.newpick4u.news.news.application.dto.NewsInfoDto;
import com.newpick4u.news.news.application.dto.NewsTagDto;
import com.newpick4u.news.news.application.dto.NewsTagDto.TagDto;
import com.newpick4u.news.news.domain.entity.News;
import com.newpick4u.news.news.domain.entity.NewsStatus;
import com.newpick4u.news.news.domain.entity.TagInbox;
import com.newpick4u.news.news.domain.repository.NewsRepository;
import com.newpick4u.news.news.domain.repository.TagInboxRepository;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = {"tag.fct.v1"})
@DirtiesContext
@ActiveProfiles("test")
class NewsTagConsumerTest {

    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private TagInboxRepository tagInboxRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;
    private Consumer<String, NewsInfoDto> newsInfoConsumer;

    @BeforeEach
    void setup() {
//        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
//
//        Map<String, Object> producerProps = KafkaTestUtils.producerProps(embeddedKafkaBroker);
//        newsInfoProducer = new DefaultKafkaProducerFactory<>(producerProps, new StringSerializer(), new StringSerializer()).createProducer();
        Map<String, Object> producerProps = KafkaTestUtils.producerProps(embeddedKafkaBroker);
        kafkaTemplate = new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(producerProps, new StringSerializer(), new StringSerializer()));

        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("news-tag-consumer", "false", embeddedKafkaBroker);
        consumerProps.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        consumerProps.put(JsonDeserializer.VALUE_DEFAULT_TYPE, NewsTagDto.class.getName());
        consumerProps.put(JsonDeserializer.KEY_DEFAULT_TYPE, String.class.getName());
        consumerProps.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumerProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        consumerProps.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 20);
        consumerProps.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 15000);
        consumerProps.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, 1000);
        consumerProps.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 60000);

        newsInfoConsumer = new DefaultKafkaConsumerFactory<>(consumerProps, new StringDeserializer(), new JsonDeserializer<>(NewsInfoDto.class)).createConsumer();
        newsInfoConsumer.subscribe(Collections.singleton("tag.fct.v1"));
    }

    @Test
    void 태그_정상_컨슈밍_및_뉴스_업데이트() throws Exception {
        // given
//        String aiNewsId = "ai-tag-news";
//        News news = News.create(aiNewsId, "제목", "내용",  "http://url.com", "2025-04-13",0L);
//        newsRepository.save(news);
//
//        List<TagDto> tagList = createSampleTags();
//        NewsTagDto dto = new NewsTagDto(aiNewsId, tagList);
//        String json = objectMapper.writeValueAsString(dto);
//
//        // when
//        kafkaTemplate.send(new ProducerRecord<>("tag.fct.v1", aiNewsId, json));
//
//        // then
//        Awaitility.await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
//            News result = newsRepository.findByAiNewsId(aiNewsId).orElseThrow();
//            assertThat(result.getNewsTagList()).hasSize(10);
//            assertThat(news.getStatus()).isEqualTo(NewsStatus.ACTIVE);
//        });
        String aiNewsId = "ai-tag-news";
        News news = News.create(aiNewsId, "제목", "내용",  "http://url.com", "2025-04-13",0L);
        newsRepository.save(news); // 뉴스는 이미 DB에 존재하는 상태

        List<TagDto> tagList = createSampleTags(); // 10개 이하
        NewsTagDto dto = new NewsTagDto(aiNewsId, tagList);
        String json = objectMapper.writeValueAsString(dto);

        // when
        kafkaTemplate.send(new ProducerRecord<>("tag.fct.v1", aiNewsId, json));

        // then
        Awaitility.await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            News result = newsRepository.findByAiNewsId(aiNewsId).orElseThrow();
            assertThat(result.getNewsTagList()).hasSize(10); // 태그 10개 저장되었는지
            assertThat(result.getStatus()).isEqualTo(NewsStatus.ACTIVE); // 상태도 업데이트 되었는지
        });

    }

    @Test
    void 태그_수신_시_뉴스_없을경우_인박스_저장() throws Exception {
        // given
        String aiNewsId = "ai-no-news";
        List<TagDto> tagList = createSampleTags();
        NewsTagDto dto = new NewsTagDto(aiNewsId, tagList);
        String json = objectMapper.writeValueAsString(dto);

        // when
        kafkaTemplate.send(new ProducerRecord<>("tag.fct.v1", aiNewsId, json));

        // then
        Awaitility.await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            List<TagInbox> inboxList = tagInboxRepository.findAll();
            assertThat(inboxList).isNotEmpty();
            assertThat(inboxList.get(0).getAiNewsId()).isEqualTo(aiNewsId);
        });
    }

    private List<TagDto> createSampleTags() {
        return List.of(
                new TagDto(UUID.randomUUID(), "정치"),
                new TagDto(UUID.randomUUID(), "경제"),
                new TagDto(UUID.randomUUID(), "사회"),
                new TagDto(UUID.randomUUID(), "문화"),
                new TagDto(UUID.randomUUID(), "기술"),
                new TagDto(UUID.randomUUID(), "건강"),
                new TagDto(UUID.randomUUID(), "과학"),
                new TagDto(UUID.randomUUID(), "스포츠"),
                new TagDto(UUID.randomUUID(), "연예"),
                new TagDto(UUID.randomUUID(), "라이프")
        );
    }
}
