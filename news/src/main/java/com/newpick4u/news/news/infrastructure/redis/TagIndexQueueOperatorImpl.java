package com.newpick4u.news.news.infrastructure.redis;


import com.newpick4u.news.news.application.usecase.TagIndexQueueOperator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class TagIndexQueueOperatorImpl implements TagIndexQueueOperator {

    private static final String TAG_INDEX_LIST = "tag:index:list";
    private static final String TAG_PENDING_SET = "tag:index:pending";

    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public void enqueuePendingTag(String tag) {
        log.info("Enqueueing tag: {}", tag);
        redisTemplate.opsForSet().add(TAG_PENDING_SET, tag);

        // 대기열에 추가된 태그가 Redis에 잘 반영되었는지 확인
        Set<String> pendingTags = redisTemplate.opsForSet().members(TAG_PENDING_SET);
        if (pendingTags != null && pendingTags.contains(tag)) {
            log.info("Tag '{}' successfully added to the pending set.", tag);  // 태그가 대기열에 제대로 들어갔는지 확인
        } else {
            log.warn("Failed to add tag '{}' to the pending set.", tag);  // 실패 시 로그 추가
        }
    }

    @Override
    public void flushPendingTagsToGlobalIndex() {
        Set<String> pending = redisTemplate.opsForSet().members(TAG_PENDING_SET);
        if (pending == null || pending.isEmpty()) return;

        List<String> existing = redisTemplate.opsForList().range(TAG_INDEX_LIST, 0, -1);
        Set<String> existingSet = new HashSet<>(existing);

        for (String tag : pending) {
            if (!existingSet.contains(tag)) {
                redisTemplate.opsForList().rightPush(TAG_INDEX_LIST, tag);
            }
            redisTemplate.opsForSet().remove(TAG_PENDING_SET, tag);
        }
    }
}
