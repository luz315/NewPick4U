package com.newpick4u.newsorigin.global.config;

import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
public class KafkaProducerConfig {

  // =======================
  //  Producer 설정
  // =======================

  // 클러스터 주소
  @Value("${app.kafka.producer.bootstrap-servers}")
  private String bootstrapServers;

  @Value("${app.kafka.producer.acks:all}")
  private String acks;

  @Value("${app.kafka.producer.retries:3}")
  private int retries;

  @Value("${app.kafka.producer.retry-backoff-ms:5000}")
  private int retryBackoffMs;

  @Value("${app.kafka.producer.enable-idempotence:true}")
  private boolean enableIdempotence;

  @Value("${app.kafka.producer.max-in-flight-requests-per-connection:5}")
  private int maxInFlightRequests;

  @Value("${app.kafka.producer.linger-ms:20}")
  private int lingerMs;

  @Value("${app.kafka.producer.batch-size:16384}") // 16KB (기본값), 32KB 가능
  private int batchSize;

  @Value("${app.kafka.producer.compression-type:none}")
  private String compressionType;

  /* =========================
   일반 Producer 설정
   ========================= */
  @Bean(name = "producerFactory")
  public ProducerFactory<String, String> ProducerFactory() {
    return new DefaultKafkaProducerFactory<>(
        producerConfigs(
            bootstrapServers,
            acks,
            retries,
            retryBackoffMs,
            enableIdempotence,
            maxInFlightRequests,
            lingerMs,
            batchSize,
            compressionType
        )
    );
  }

  @Bean(name = "kafkaTemplate")
  public KafkaTemplate<String, String> KafkaTemplate(
      @Qualifier("producerFactory") ProducerFactory<String, String> factory) {
    return new KafkaTemplate<>(factory);
  }

  /* =========================
     공통 Producer 설정 메서드
     ========================= */
  private Map<String, Object> producerConfigs(
      String bootstrapServers,
      String acks,
      int retries,
      int retryBackoffMs,
      boolean enableIdempotence,
      int maxInFlightRequests,
      int lingerMs,
      int batchSize,
      String compressionType
  ) {
    Map<String, Object> props = new HashMap<>();

    // Kafka 클러스터 주소
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

    // 직렬화 방식
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

    // 안정성 관련 설정
    props.put(ProducerConfig.ACKS_CONFIG, acks); // 메시지 전송 보장 수준
    props.put(ProducerConfig.RETRIES_CONFIG, retries); // 전송 실패 시 재시도 횟수
    props.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, retryBackoffMs); // 재시도 간격 (ms)
    props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, enableIdempotence); // 중복 전송 방지

    // Kafka Producer가 단일 커넥션(connection)에서 동시에 전송할 수 있는 요청 수의 최대치
    // 브로커의 응답을 기다리지 않고, 한 번에 몇 개의 레코드를 병렬로 보낼 수 있는가
    // 동시에 보낼 요청의 수
    props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, maxInFlightRequests);

    // 성능 최적화 설정
    props.put(ProducerConfig.LINGER_MS_CONFIG, lingerMs); // 배치 전송 대기 시간 (ms)
    // 배치 크기 (bytes) : 한 요청에 담을 데이터의 크기
    props.put(ProducerConfig.BATCH_SIZE_CONFIG, batchSize);
    props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, compressionType); // 압축 방식

    return props;
  }
}
