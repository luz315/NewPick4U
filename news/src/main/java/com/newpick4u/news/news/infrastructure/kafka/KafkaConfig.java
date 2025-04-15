package com.newpick4u.news.news.infrastructure.kafka;

import com.newpick4u.news.news.application.dto.NewsInfoDto;
import com.newpick4u.news.news.application.dto.NewsTagDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
@Slf4j
public class KafkaConfig {
    private static final String PACKAGE_TRUSTED = "*";

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public ProducerFactory<String, String> producerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public KafkaTemplate<String, Object> dlqKafkaTemplate() {
        return new KafkaTemplate<>(dlqProducerFactory());
    }

    @Bean
    public ProducerFactory<String, Object> dlqProducerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(config);
    }

    private Map<String, Object> commonConsumerProps(String groupId) {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 20);
        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 15000);
        props.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, 1000);
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 60000);
        return props;
    }

    private <T> ConsumerFactory<String, T> buildConsumerFactory(Class<T> clazz, String groupId) {
        JsonDeserializer<T> valueDeserializer = new JsonDeserializer<>(clazz);
        valueDeserializer.addTrustedPackages(PACKAGE_TRUSTED);
        valueDeserializer.setRemoveTypeHeaders(false);
        valueDeserializer.setUseTypeMapperForKey(false);
        return new DefaultKafkaConsumerFactory<>(
                commonConsumerProps(groupId),
                new StringDeserializer(),
                valueDeserializer
        );
    }

    private <T> ConcurrentKafkaListenerContainerFactory<String, T> buildListenerContainerFactory(
            ConsumerFactory<String, T> consumerFactory
    ) {
        ConcurrentKafkaListenerContainerFactory<String, T> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        return factory;
    }

    // ai - news 카프카
    @Bean
    public ConsumerFactory<String, NewsInfoDto> newsInfoConsumerFactory() {
        return buildConsumerFactory(NewsInfoDto.class, "news-info-consumer");
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, NewsInfoDto> newsInfoListenerContainerFactory(
            ConsumerFactory<String, NewsInfoDto> newsInfoConsumerFactory,
            KafkaTemplate<String, Object> dlqKafkaTemplate

    ) {
        var factory = buildListenerContainerFactory(newsInfoConsumerFactory);

        var recoverer = new DeadLetterPublishingRecoverer(dlqKafkaTemplate,
                (record, ex) -> {log.warn("[DLQ] 전송 대상 메시지 - key: {}, value: {}, error: {}", record.key(), record.value(), ex.getMessage());
                    return new TopicPartition("news-info-dlq.fct.v1", record.partition());
                });

        var errorHandler = new DefaultErrorHandler(recoverer, new FixedBackOff(1000L, 3));
        errorHandler.addRetryableExceptions(RuntimeException.class);
        errorHandler.setRetryListeners((record, ex, deliveryAttempt) -> {
            log.warn("[Retrying] {}번째 재시도 중 - key: {}, value: {}", deliveryAttempt, record.key(), record.value());
        });

        factory.setCommonErrorHandler(errorHandler);
        factory.setConcurrency(1);
        return factory;
    }

    // tag - news 카프카
    @Bean
    public ConsumerFactory<String, NewsTagDto> newsTagConsumerFactory() {
        return buildConsumerFactory(NewsTagDto.class, "news-tag-consumer");
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, NewsTagDto> newsTagListenerContainerFactory(
            ConsumerFactory<String, NewsTagDto> newsTagConsumerFactory,
            KafkaTemplate<String, Object> dlqKafkaTemplate
    ) {
        var factory = buildListenerContainerFactory(newsTagConsumerFactory);

        var recoverer = new DeadLetterPublishingRecoverer(dlqKafkaTemplate,
                (record, ex) -> {log.warn("[DLQ] 전송 대상 메시지 - key: {}, value: {}, error: {}", record.key(), record.value(), ex.getMessage());
                    return new TopicPartition("tag-dlq.fct.v1", record.partition());
                });

        var errorHandler = new DefaultErrorHandler(recoverer, new FixedBackOff(1000L, 3));
        errorHandler.addRetryableExceptions(RuntimeException.class);
        errorHandler.setRetryListeners((record, ex, deliveryAttempt) -> {
            log.warn("[Retrying] {}번째 재시도 중 - key: {}, value: {}", deliveryAttempt, record.key(), record.value());
        });

        factory.setCommonErrorHandler(errorHandler);
        factory.setConcurrency(1);

        return factory;
    }
}