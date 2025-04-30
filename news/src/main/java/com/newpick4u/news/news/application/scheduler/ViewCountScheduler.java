package com.newpick4u.news.news.application.scheduler;

import com.newpick4u.news.news.domain.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ViewCountScheduler {

    private final RedisTemplate<String, String> redisTemplate;
    private final NewsRepository newsRepository;
    private static final String VIEW_COUNT_KEY_PREFIX = "view:";

    @Scheduled(cron = "0 * * * * *") // 매 1분마다 실행
    public void syncViewCounts() {
        Set<ZSetOperations.TypedTuple<String>> entries = redisTemplate.opsForZSet().rangeWithScores(VIEW_COUNT_KEY_PREFIX, 0, -1);

        if (entries == null || entries.isEmpty()) return;

        entries.parallelStream().forEach(tuple -> {
            String newsIdStr = tuple.getValue();
            Double score = tuple.getScore();
            if (newsIdStr == null || score == null || score <= 0) return;

            UUID newsId = UUID.fromString(newsIdStr);
            long count = score.longValue();

            try {
                // DB 반영
                newsRepository.incrementViewCount(newsId, count);
                // Redis 차감 (decrement)
                redisTemplate.opsForZSet().incrementScore(VIEW_COUNT_KEY_PREFIX, newsIdStr, -count);
                log.info("[ViewSync] 동기화 완료 - newsId={}, +{}회 반영", newsId, count);
            } catch (Exception e) {
                log.error("[ViewSync] 동기화 실패 - newsId={}", newsId, e);
            }
        });
    }
} 
