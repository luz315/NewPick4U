package com.newpick4u.comment.comment.infrastructure.cache;

import com.newpick4u.comment.comment.application.TagCacheRepository;
import jakarta.annotation.PostConstruct;
import java.time.Duration;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class TagCacheRepositoryImpl implements TagCacheRepository {

  private static final String TAG_COUNT_ZSET_KEY = "tag_count";
  private static final Duration TAG_TTL = Duration.ofMinutes(5);
  private static final String TAG_TTL_KEY_PREFIX = "tag:ttl:";

  private final RedisTemplate<String, String> redisTemplate;
  private ZSetOperations<String, String> zSetOperations;
  private ValueOperations<String, String> valueOperations;

  @PostConstruct
  public void init() {
    zSetOperations = redisTemplate.opsForZSet();
    valueOperations = redisTemplate.opsForValue();
  }

  // 태그 정보 캐싱
  public void increaseTagCount(List<String> tags) {
    for (String tag : tags) {
      zSetOperations.incrementScore(TAG_COUNT_ZSET_KEY, tag, 1);        // 태그 스코어 정보 증가
      valueOperations.set(TAG_TTL_KEY_PREFIX + tag, "1", TAG_TTL); // 태그 TTL 초기화
    }
  }

  // TTL 만료 키 삭제 작업
  public void deleteByTTL() {

  }
}
