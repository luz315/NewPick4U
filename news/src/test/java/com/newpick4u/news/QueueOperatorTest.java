package com.newpick4u.news;

import com.newpick4u.news.news.application.usecase.*;
import com.newpick4u.news.news.domain.entity.News;
import com.newpick4u.news.news.domain.repository.NewsRepository;
import com.newpick4u.news.news.infrastructure.redis.NewsVectorQueueOperatorImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;


import java.util.List;

@SpringBootTest
@ActiveProfiles("test")  // 테스트 환경에서 실행되도록 설정
class QueueOperatorTest {

    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private NewsVectorQueueOperatorImpl newsVectorQueueOperator;

    @Autowired
    private TagIndexQueueOperator tagIndexQueueOperator;

    @Test
    @Transactional
    void 테스트뉴스DB_대기열_추가_테스트() throws Exception {
        // given: 모든 뉴스와 태그를 가져옴
        List<News> allNews = newsRepository.findAll();  // 모든 뉴스 가져오기
        // when: 각 뉴스에 대해 대기열 작업을 진행
        for (News news : allNews) {
            // 1. 뉴스 벡터 대기열에 넣기 제가볼때는 예외인거 같아요 그 디버깅으로 보면 다시디버깅해볼게요실행이 아예안돼요 왜냐면 제가
            System.out.println("뉴스아이디 (테스트 중): " + news.getId()); //요기는됨되게 웃긴건 요기는 돼요
            newsVectorQueueOperator.enqueueNewsVector(news.getId()); //요기안됨근데 여기가 안돼요 미치겟멘
            System.out.println("뉴스아이디 (enqueue 후): " + news.getId());
            // 2. 뉴스 태그 대기열에 넣기
            news.getNewsTagList().forEach(tag -> {
                tagIndexQueueOperator.enqueuePendingTag(tag.getName());
                // 로그를 추가해서 태그가 들어가는지 확인
                System.out.println("Tag enqueued: " + tag.getName());
            });
        }

        Thread.sleep(1000);  // 1초 정도 대기

        // then: 대기열에 넣은 태그와 벡터 작업이 잘 들어갔는지 검증
        for (News news : allNews) {
            // 1. 뉴스 벡터가 대기열에 잘 들어갔는지 확인
            boolean isNewsInQueue = redisTemplate.opsForSet().isMember("news:vector:pending", news.getId().toString());
            assertThat(isNewsInQueue).isTrue();  // 뉴스 벡터가 대기열에 들어갔는지 확인

            // 2. 뉴스 태그들이 대기열에 잘 들어갔는지 확인
            news.getNewsTagList().forEach(tag -> {
                boolean isTagInQueue = redisTemplate.opsForSet().isMember("tag:index:pending", tag.getName());
                assertThat(isTagInQueue).isTrue();  // 태그가 대기열에 들어갔는지 확인
            });
        }
    }
}