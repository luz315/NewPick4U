package com.newpick4u.comment.comment.infrastructure.cache;

import jakarta.annotation.PostConstruct;
import java.time.Duration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@ActiveProfiles("test")
@TestInstance(Lifecycle.PER_CLASS)
@MockitoBean(types = ScheduledAnnotationBeanPostProcessor.class) // @Scheduled 무력화
@SpringBootTest
class TagCacheRepositoryImplTest {

  @Autowired
  RedisTemplate<String, String> redisTemplate;

  @Autowired
  TagCacheRepositoryImpl tagCacheRepository;

  ZSetOperations<String, String> zSetOperations;
  ValueOperations<String, String> valueOperations;
  String TAG_COUNT_ZSET_KEY = "tag_count";
  Duration TAG_TTL = Duration.ofMinutes(5);
  String TAG_TTL_KEY_PREFIX = "tag:ttl:";

  @PostConstruct
  void init() {
    zSetOperations = redisTemplate.opsForZSet();
    valueOperations = redisTemplate.opsForValue();
  }

  @Test
  @DisplayName("TTL 만료 삭제 테스트")
  void deleteTagScoreCacheByTTLTest() {
//    // given
//
//    for (int i = 0; i < 3; i++) {
//      String tagName = "테스트태그" + i;
//      zSetOperations.incrementScore(TAG_COUNT_ZSET_KEY, tagName, 1); // 태그 스코어 정보 증가
//      if (i == 0) {
//        valueOperations.set(TAG_TTL_KEY_PREFIX + tagName, "1", TAG_TTL);    // 태그 TTL 초기화
//      }
//    }
//
//    // when
//    tagCacheRepository.deleteTagScoreCacheByTTL();
//
//    // then
//    Set<String> tagSet = zSetOperations.range(TAG_COUNT_ZSET_KEY, 0, -1);
//    Assertions.assertTrue(tagSet.contains("테스트태그0"));
//    Assertions.assertFalse(tagSet.contains("테스트태그1"));
//    Assertions.assertFalse(tagSet.contains("테스트태그2"));
  }

  @AfterAll
  void clearTestCache() {
//    zSetOperations.remove(TAG_COUNT_ZSET_KEY, "테스트태그" + 0);
//    redisTemplate.delete(TAG_TTL_KEY_PREFIX + "테스트태그" + 0);
  }
}