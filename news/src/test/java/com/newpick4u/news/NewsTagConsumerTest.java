//package com.newpick4u.news;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.newpick4u.news.news.application.dto.NewsInfoDto;
//import com.newpick4u.news.news.application.dto.NewsTagDto;
//import com.newpick4u.news.news.application.dto.NewsTagDto.TagDto;
//import com.newpick4u.news.news.domain.entity.News;
//import com.newpick4u.news.news.domain.entity.NewsStatus;
//import com.newpick4u.news.news.domain.entity.TagInbox;
//import com.newpick4u.news.news.domain.repository.NewsRepository;
//import com.newpick4u.news.news.domain.repository.TagInboxRepository;
//import com.newpick4u.news.news.infrastructure.jpa.JpaNewsRepository;
//import com.newpick4u.news.news.infrastructure.jpa.JpaNewsTagRepository;
//import com.newpick4u.news.news.infrastructure.kafka.KafkaConfig;
//import com.newpick4u.news.news.infrastructure.kafka.NewsInfoConsumer;
//import org.apache.kafka.clients.consumer.Consumer;
//import org.apache.kafka.clients.consumer.ConsumerConfig;
//import org.apache.kafka.clients.producer.Producer;
//import org.apache.kafka.clients.producer.ProducerRecord;
//import org.apache.kafka.common.serialization.StringDeserializer;
//import org.apache.kafka.common.serialization.StringSerializer;
//import org.awaitility.Awaitility;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.context.annotation.Import;
//import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
//import org.springframework.kafka.core.DefaultKafkaProducerFactory;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.kafka.support.serializer.JsonDeserializer;
//import org.springframework.kafka.test.EmbeddedKafkaBroker;
//import org.springframework.kafka.test.context.EmbeddedKafka;
//import org.springframework.kafka.test.utils.KafkaTestUtils;
//import org.springframework.test.annotation.DirtiesContext;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.Collections;
//import java.util.List;
//import java.util.Map;
//import java.util.UUID;
//import java.util.concurrent.TimeUnit;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//
//@SpringBootTest(properties = {
//        "eureka.client.enabled=false"
//})
//@ActiveProfiles("test")
//@EmbeddedKafka(partitions = 1, topics = {"tag.fct.v1", "tag-dlq.fct.v1"})
//@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) // 이게 있으면 상태가 초기화됩니다
//@Import({KafkaConfig.class, NewsInfoConsumer.class})
//class NewsTagConsumerTest {
//
//    @Autowired
//    private NewsRepository newsRepository;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Autowired
//    private KafkaTemplate<String, String> kafkaTemplate;
//
//    @Autowired
//    private EmbeddedKafkaBroker embeddedKafkaBroker;
//
//    @Autowired
//    private TagInboxRepository tagInboxRepository;
//
//    @Test
//    void 태그_정상_컨슈밍_및_뉴스_업데이트() throws Exception {
//        // given
//        String aiNewsId = "ai-tag-news";
//        News news = News.create(aiNewsId, "제목", "내용",  "http://url.com", "2025-04-13",0L);
//        newsRepository.save(news);
//
//        List<NewsTagDto.TagDto> tagList = createSampleTags();
//        NewsTagDto dto = new NewsTagDto(aiNewsId, tagList);
//        String json = objectMapper.writeValueAsString(dto);
//
//        // when
//        kafkaTemplate.send("tag.fct.v1", aiNewsId, json);
//
//        // then
//        Awaitility.await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
//
//            News result = JpaNewsTagRepository.findWithTagsByAiNewsId(aiNewsId).orElseThrow();
//            assertThat(result.getNewsTagList()).hasSize(10);
//            assertThat(result.getStatus()).isEqualTo(NewsStatus.ACTIVE);
//        });
//    }
//
//    private List<NewsTagDto.TagDto> createSampleTags() {
//        return List.of(
//                new NewsTagDto.TagDto(UUID.randomUUID(), "정치"),
//                new NewsTagDto.TagDto(UUID.randomUUID(), "경제"),
//                new NewsTagDto.TagDto(UUID.randomUUID(), "사회"),
//                new NewsTagDto.TagDto(UUID.randomUUID(), "문화"),
//                new NewsTagDto.TagDto(UUID.randomUUID(), "기술"),
//                new NewsTagDto.TagDto(UUID.randomUUID(), "건강"),
//                new NewsTagDto.TagDto(UUID.randomUUID(), "과학"),
//                new NewsTagDto.TagDto(UUID.randomUUID(), "스포츠"),
//                new NewsTagDto.TagDto(UUID.randomUUID(), "연예"),
//                new NewsTagDto.TagDto(UUID.randomUUID(), "라이프")
//        );
//    }
//
////    @Test
////    void 태그_수신_시_뉴스_없을경우_인박스_저장() throws Exception {
////        // given
////        String aiNewsId = "ai-no-news";
////        List<TagDto> tagList = createSampleTags();
////        NewsTagDto dto = new NewsTagDto(aiNewsId, tagList);
////        String json = objectMapper.writeValueAsString(dto);
////
////        // when
////        kafkaTemplate.send(new ProducerRecord<>("tag.fct.v1", aiNewsId, json));
////
////        // then
////        Awaitility.await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
////            List<TagInbox> inboxList = tagInboxRepository.findAll();
////            assertThat(inboxList).isNotEmpty();
////            assertThat(inboxList.get(0).getAiNewsId()).isEqualTo(aiNewsId);
////        });
////    }
////
////    private List<TagDto> createSampleTags() {
////        return List.of(
////                new TagDto(UUID.randomUUID(), "정치"),
////                new TagDto(UUID.randomUUID(), "경제"),
////                new TagDto(UUID.randomUUID(), "사회"),
////                new TagDto(UUID.randomUUID(), "문화"),
////                new TagDto(UUID.randomUUID(), "기술"),
////                new TagDto(UUID.randomUUID(), "건강"),
////                new TagDto(UUID.randomUUID(), "과학"),
////                new TagDto(UUID.randomUUID(), "스포츠"),
////                new TagDto(UUID.randomUUID(), "연예"),
////                new TagDto(UUID.randomUUID(), "라이프")
////        );
////    }
//}
