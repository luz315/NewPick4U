package com.newpick4u.news.news.infrastructure.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.newpick4u.news.news.application.usecase.NewsVectorQueueOperator;
import com.newpick4u.news.news.domain.entity.News;
import com.newpick4u.news.news.domain.repository.NewsRepository;
import com.newpick4u.news.news.application.usecase.VectorConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import javax.crypto.Cipher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class NewsVectorQueueOperatorImpl implements NewsVectorQueueOperator {
    private static final String VECTOR_KEY_PREFIX = "news:vector:";
    private static final String QUEUE_KEY = "news:vector:pending";

    private final RedisTemplate<String, String> redisTemplate;
    private final NewsRepository newsRepository;
    private final VectorConverter vectorConverter;
    private final ObjectMapper objectMapper;

    @Override
    public void enqueueNewsVector(UUID newsId) {
        log.info("엔큐들어오고 난 직전 id: {}", newsId);

        Optional<News> newsOpt = newsRepository.findById(newsId);
        if (newsOpt.isEmpty()) {
            log.warn("News not found for ID: {}", newsId);
        } else {
            log.info("Enqueuing news ID to Redis: {}", newsId);
        }
        redisTemplate.opsForSet().add(QUEUE_KEY, newsId.toString());
    }

    @Override
    public void removeFromQueue(UUID newsId) {
        redisTemplate.opsForSet().remove(QUEUE_KEY, newsId.toString());
    }

    @Override
    @Transactional
    public void flushAndGeneratePendingVectors() {
        Set<String> pendingIds = redisTemplate.opsForSet().members(QUEUE_KEY);
        if (pendingIds == null || pendingIds.isEmpty()) {
            log.info("No pending news IDs found in the vector queue.");
            return;
        }

        List<String> tagIndexList = redisTemplate.opsForList().range("tag:index:list", 0, -1);
        if (tagIndexList == null || tagIndexList.isEmpty()) {
            log.warn("Tag index list is empty, unable to generate vectors.");
            return;  // 태그 인덱스가 비어 있으면 벡터 생성 진행 불가
        }
        for (String idStr : pendingIds) {
            try {
                UUID newsId = UUID.fromString(idStr);
                Optional<News> newsOpt = newsRepository.findById(newsId);
                if (newsOpt.isEmpty()) {
                    log.warn("News not found for ID: {}", newsId);
                    continue;  // 뉴스가 없으면 벡터 생성 안함
                }

                News news = newsOpt.get();
                double[] vector = vectorConverter.toNewsVector(news, tagIndexList);
                String json = objectMapper.writeValueAsString(vector);

                redisTemplate.opsForValue().set("news:vector:" + newsId, json);
                removeFromQueue(newsId);
                log.info("Saving vector for news ID: {}", newsId);

            } catch (Exception e) {
                log.error("[뉴스 벡터 생성 실패] id={}, err={}", idStr, e.getMessage());
            }
        }
    }

    public Optional<double[]> getVector(UUID newsId) {
        try {
            String json = redisTemplate.opsForValue().get(VECTOR_KEY_PREFIX + newsId);
            if (json == null) return Optional.empty();
            return Optional.of(objectMapper.readValue(json, double[].class));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
