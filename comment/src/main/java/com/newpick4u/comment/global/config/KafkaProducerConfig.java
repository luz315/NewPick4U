package com.newpick4u.comment.global.config;

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
  // NORMAL Producer 설정
  // =======================

  // 전송 일반케이스 클러스터 주소
  @Value("${app.kafka.producer.normal.bootstrap-servers}")
  private String NORMAL_BOOTSTRAP_SERVERS;

  @Value("${app.kafka.producer.normal.acks:all}")
  private String NORMAL_ACKS;

  @Value("${app.kafka.producer.normal.retries:3}")
  private int NORMAL_RETRIES;

  @Value("${app.kafka.producer.normal.retry-backoff-ms:5000}")
  private int NORMAL_RETRY_BACKOFF_MS;

  @Value("${app.kafka.producer.normal.enable-idempotence:true}")
  private boolean NORMAL_ENABLE_IDEMPOTENCE;

  @Value("${app.kafka.producer.normal.max-in-flight-requests-per-connection:5}")
  private int NORMAL_MAX_IN_FLIGHT_REQUESTS;

  @Value("${app.kafka.producer.normal.linger-ms:20}")
  private int NORMAL_LINGER_MS;

  @Value("${app.kafka.producer.normal.batch-size:16384}") // 16KB (기본값), 32KB 가능
  private int NORMAL_BATCH_SIZE;

  @Value("${app.kafka.producer.normal.compression-type:none}")
  private String NORMAL_COMPRESSION_TYPE;

  // =======================
  // EXCEPTIONAL Producer 설정
  // =======================

  // 전송 예외케이스 DLQ 클러스터 주소
  @Value("${app.kafka.producer.exceptional.bootstrap-servers}")
  private String EXCEPTIONAL_BOOTSTRAP_SERVERS;

  @Value("${app.kafka.producer.exceptional.acks:all}")
  private String EXCEPTIONAL_ACKS;

  @Value("${app.kafka.producer.exceptional.retries:5}")
  private int EXCEPTIONAL_RETRIES;

  @Value("${app.kafka.producer.exceptional.retry-backoff-ms:3000}")
  private int EXCEPTIONAL_RETRY_BACKOFF_MS;

  @Value("${app.kafka.producer.exceptional.enable-idempotence:true}")
  private boolean EXCEPTIONAL_ENABLE_IDEMPOTENCE;

  @Value("${app.kafka.producer.exceptional.max-in-flight-requests-per-connection:3}")
  private int EXCEPTIONAL_MAX_IN_FLIGHT_REQUESTS;

  @Value("${app.kafka.producer.exceptional.linger-ms:10}")
  private int EXCEPTIONAL_LINGER_MS;

  @Value("${app.kafka.producer.exceptional.batch-size:32768}") // 16KB (기본값), 32KB 가능
  private int EXCEPTIONAL_BATCH_SIZE;

  @Value("${app.kafka.producer.exceptional.compression-type:snappy}")
  private String EXCEPTIONAL_COMPRESSION_TYPE;


  /* =========================
   일반 Producer 설정
   ========================= */
  @Bean(name = "normalProducerFactory")
  public ProducerFactory<String, String> normalProducerFactory() {
    return new DefaultKafkaProducerFactory<>(
        producerConfigs(
            NORMAL_BOOTSTRAP_SERVERS,
            NORMAL_ACKS,
            NORMAL_RETRIES,
            NORMAL_RETRY_BACKOFF_MS,
            NORMAL_ENABLE_IDEMPOTENCE,
            NORMAL_MAX_IN_FLIGHT_REQUESTS,
            NORMAL_LINGER_MS,
            NORMAL_BATCH_SIZE,
            NORMAL_COMPRESSION_TYPE
        )
    );
  }

  @Bean(name = "normalKafkaTemplate")
  public KafkaTemplate<String, String> normalKafkaTemplate(
      @Qualifier("normalProducerFactory") ProducerFactory<String, String> factory) {
    return new KafkaTemplate<>(factory);
  }

  /* =========================
   예외 Producer 설정
   ========================= */
  @Bean(name = "exceptionProducerFactory")
  public ProducerFactory<String, String> exceptionProducerFactory() {
    return new DefaultKafkaProducerFactory<>(
        producerConfigs(
            EXCEPTIONAL_BOOTSTRAP_SERVERS,
            EXCEPTIONAL_ACKS,
            EXCEPTIONAL_RETRIES,
            EXCEPTIONAL_RETRY_BACKOFF_MS,
            EXCEPTIONAL_ENABLE_IDEMPOTENCE,
            EXCEPTIONAL_MAX_IN_FLIGHT_REQUESTS,
            EXCEPTIONAL_LINGER_MS,
            EXCEPTIONAL_BATCH_SIZE,
            EXCEPTIONAL_COMPRESSION_TYPE
        )
    );
  }

  @Bean(name = "exceptionKafkaTemplate")
  public KafkaTemplate<String, String> exceptionKafkaTemplate(
      @Qualifier("exceptionProducerFactory") ProducerFactory<String, String> factory) {
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
