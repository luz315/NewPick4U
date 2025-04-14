package com.newpick4u.comment.global.config;

import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties.AckMode;

@Configuration
public class KafkaConsumerConfig {

  // -------------------- [ NORMAL CONSUMER 설정값 주입 ] --------------------

  @Value("${app.kafka.consumer.normal.bootstrap-servers}")
  private String NORMAL_BOOTSTRAP_SERVERS;

  @Value("${app.kafka.consumer.normal.enable-auto-commit:true}")
  private boolean NORMAL_ENABLE_AUTO_COMMIT;

  @Value("${app.kafka.consumer.normal.enable-spring-ackmode-immediate:false}")
  private boolean NORMAL_ENABLE_SPRING_ACKMODE_IMMEDIATE;

  @Value("${app.kafka.consumer.normal.auto-offset-reset:latest}")
  private String NORMAL_AUTO_OFFSET_RESET;

  @Value("${app.kafka.consumer.normal.max-poll-records:500}")
  private int NORMAL_MAX_POLL_RECORDS;

  @Value("${app.kafka.consumer.normal.max-poll-interval-ms:300000}")
  private int NORMAL_MAX_POLL_INTERVAL_MS;

  @Value("${app.kafka.consumer.normal.session-timeout-ms:10000}")
  private int NORMAL_SESSION_TIMEOUT_MS;

  @Value("${app.kafka.consumer.normal.concurrency:1}")
  private int NORMAL_CONCURRENCY;

// -------------------- [ EXCEPTION CONSUMER 설정값 주입 ] --------------------

  @Value("${app.kafka.consumer.exception.bootstrap-servers}")
  private String EXCEPTION_BOOTSTRAP_SERVERS;

  @Value("${app.kafka.consumer.exception.enable-auto-commit:true}")
  private boolean EXCEPTION_ENABLE_AUTO_COMMIT;

  @Value("${app.kafka.consumer.exception.enable-spring-ackmode-immediate:false}")
  private boolean EXCEPTION_ENABLE_SPRING_ACKMODE_IMMEDIATE;

  @Value("${app.kafka.consumer.exception.auto-offset-reset:latest}")
  private String EXCEPTION_AUTO_OFFSET_RESET;

  @Value("${app.kafka.consumer.exception.max-poll-records:500}")
  private int EXCEPTION_MAX_POLL_RECORDS;

  @Value("${app.kafka.consumer.exception.max-poll-interval-ms:300000}")
  private int EXCEPTION_MAX_POLL_INTERVAL_MS;

  @Value("${app.kafka.consumer.exception.session-timeout-ms:10000}")
  private int EXCEPTION_SESSION_TIMEOUT_MS;

  @Value("${app.kafka.consumer.exception.concurrency:1}")
  private int EXCEPTION_CONCURRENCY;

  // -------------------- [ 공통 설정 생성 메서드 ] --------------------

  /**
   * Kafka Consumer 설정 공통 메서드 - 각 Consumer의 연결 정보 및 동작 방식을 지정
   */
  private Map<String, Object> consumerConfigs(
      String bootstrapServers,     // Kafka 서버 주소 (복수일 경우 쉼표로 구분)
      boolean enableAutoCommit,    // 자동 offset 커밋 사용 여부
      String autoOffsetReset,      // 초기 offset이 없거나 유효하지 않을 때의 처리 전략
      int maxPollRecords,          // 한 번에 poll() 할 수 있는 최대 레코드 수
      int maxPollIntervalMs,       // poll 간 최대 허용 간격 (비정상 종료 방지용)
      int sessionTimeoutMs         // consumer heartbeat 실패로 간주되는 시간 (서버와의 세션 유지 시간)
  ) {
    Map<String, Object> props = new HashMap<>();

    // Kafka 클러스터 주소
    // - 복수의 브로커 주소를 쉼표(,)로 구분하여 명시 가능
    // - 클러스터 내 하나 이상만 연결되면 metadata를 통해 전체 파악함
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

    // Consumer Group ID
    // - 같은 groupId를 가진 Consumer는 하나의 group으로 간주됨
    // - 같은 topic을 구독할 경우 메시지를 분산 처리하게 됨
    // - 서로 다른 groupId면 각각 모든 메시지를 수신함 (복제 처리)
    // props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId); // 리스너 쪽에서 개별 설정

    // 자동 offset 커밋 여부
    // - true면 poll() 이후 offset을 자동으로 커밋함
    // - false로 설정하고 수동 커밋(commitSync/commitAsync)하는 방식이 더 안전한 경우도 있음
    props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, enableAutoCommit);

    // offset 초기화 전략
    // - consumer group의 offset 정보가 없을 때 어디서부터 읽을지 지정
    // - earliest: 가장 오래된 메시지부터 읽음
    // - latest: 가장 최근 메시지부터 읽음 (Kafka 기본값)
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);

    // 한 번의 poll() 호출에서 가져올 수 있는 최대 레코드 수
    // - consumer 처리량 조절 시 사용
    // - 클라이언트 로직의 batch size 기준과 연동하여 조정 가능
    props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecords);

    // 최대 poll 간격 (ms)
    // - consumer가 poll()을 호출하지 않고 처리만 하고 있을 경우 session 유지 허용 시간
    // - 이 시간이 초과되면 group coordinator가 consumer를 죽은 것으로 간주하고 rebalance 발생
    props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, maxPollIntervalMs);

    // 세션 타임아웃 시간 (ms)
    // - consumer가 broker에 heartbeat를 보내지 않으면 이 시간 이후 죽은 것으로 간주됨
    // - 일반적으로 6~10초 사이를 많이 사용
    props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, sessionTimeoutMs);

    // Key 역직렬화 방식 지정
    // - Kafka는 메시지를 byte[]로 처리하므로, 적절한 역직렬화기가 필요
    // - 대부분의 경우 key는 문자열이므로 StringDeserializer 사용
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

    // Value 역직렬화 방식 지정
    // - 메시지 본문 (payload)의 역직렬화 방식
    // - JSON, Avro, Protobuf 등의 포맷을 사용하는 경우 별도의 deserializer 설정 필요
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

    return props;
  }

  // -------------------- [ NORMAL ConsumerFactory 및 ContainerFactory ] --------------------

  @Bean(name = "normalKafkaListenerContainerFactory")
  public ConcurrentKafkaListenerContainerFactory<String, String> normalKafkaListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, String> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(normalConsumerFactory());
    factory.setConcurrency(NORMAL_CONCURRENCY);
    if (NORMAL_ENABLE_SPRING_ACKMODE_IMMEDIATE
        && !NORMAL_ENABLE_AUTO_COMMIT) {
      factory.getContainerProperties().setAckMode(AckMode.MANUAL_IMMEDIATE); // 수동 커밋 즉시 적용
    }
    return factory;
  }

  @Bean(name = "normalConsumerFactory")
  public ConsumerFactory<String, String> normalConsumerFactory() {
    return new DefaultKafkaConsumerFactory<>(
        consumerConfigs(
            NORMAL_BOOTSTRAP_SERVERS,
            NORMAL_ENABLE_AUTO_COMMIT,
            NORMAL_AUTO_OFFSET_RESET,
            NORMAL_MAX_POLL_RECORDS,
            NORMAL_MAX_POLL_INTERVAL_MS,
            NORMAL_SESSION_TIMEOUT_MS
        )
    );
  }

  // -------------------- [ EXCEPTION ConsumerFactory 및 ContainerFactory ] --------------------

  @Bean(name = "exceptionKafkaListenerContainerFactory")
  public ConcurrentKafkaListenerContainerFactory<String, String> exceptionKafkaListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, String> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(exceptionConsumerFactory());
    factory.setConcurrency(EXCEPTION_CONCURRENCY);
    if (EXCEPTION_ENABLE_SPRING_ACKMODE_IMMEDIATE
        && !EXCEPTION_ENABLE_AUTO_COMMIT) {
      factory.getContainerProperties().setAckMode(AckMode.MANUAL_IMMEDIATE); // 수동 커밋 즉시 적용
    }
    return factory;
  }

  @Bean(name = "exceptionConsumerFactory")
  public ConsumerFactory<String, String> exceptionConsumerFactory() {
    return new DefaultKafkaConsumerFactory<>(
        consumerConfigs(
            EXCEPTION_BOOTSTRAP_SERVERS,
            EXCEPTION_ENABLE_AUTO_COMMIT,
            EXCEPTION_AUTO_OFFSET_RESET,
            EXCEPTION_MAX_POLL_RECORDS,
            EXCEPTION_MAX_POLL_INTERVAL_MS,
            EXCEPTION_SESSION_TIMEOUT_MS
        )
    );
  }
}
