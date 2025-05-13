package com.newpick4u.news.news.infrastructure.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.newpick4u.common.exception.CustomException;
import com.newpick4u.news.global.exception.NewsErrorCode;
import com.newpick4u.news.news.application.usecase.NewsVectorQueueOperator;
import com.newpick4u.news.news.domain.entity.News;
import com.newpick4u.news.news.domain.repository.NewsRepository;
import com.newpick4u.news.news.application.usecase.VectorConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
        if (newsRepository.findById(newsId).isEmpty()) {
            log.warn("벡터 대기열 등록 실패 - 해당 뉴스를 찾을 수 없습니다. newsId={}", newsId);
            return;
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

        if (tagIndexList.isEmpty()) {
            log.warn("태그 인덱스 리스트가 비어있어 벡터를 생성할 수 없습니다.");
            return;
        }

        for (String idStr : pendingIds) {
            processSingleNewsVector(idStr, tagIndexList);
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

    private void processSingleNewsVector(String idStr, List<String> tagIndexList) {
        try {
            UUID newsId = UUID.fromString(idStr);
            Optional<News> newsOpt = newsRepository.findById(newsId);

            if (newsOpt.isEmpty()) {
                log.warn("벡터 생성 실패 - 해당 뉴스를 찾을 수 없습니다. newsId={}", newsId);
                return;
            }

            News news = newsOpt.get();
            double[] vector = vectorConverter.toNewsVector(news, tagIndexList);
            String newsVectorJson = objectMapper.writeValueAsString(vector);

            redisTemplate.opsForValue().set(VECTOR_KEY_PREFIX + newsId, newsVectorJson);
            removeFromQueue(newsId);
            log.info("뉴스 벡터" +
                    " 생성 및 저장 완료. newsId={}", newsId);

        } catch (Exception e) {
            log.error("뉴스 벡터 생성 중 예외 발생. id={}, 에러={}", idStr, e.getMessage());
            throw CustomException.from(NewsErrorCode.VECTOR_GENERATION_FAIL);
        }
    }
}
