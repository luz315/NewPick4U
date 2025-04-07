package com.newpick4u.newsorigin.newsorigin.application.usecase;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.newpick4u.newsorigin.newsorigin.application.dto.SendNewOriginDto;
import com.newpick4u.newsorigin.newsorigin.domain.entity.NewsOrigin;
import com.newpick4u.newsorigin.newsorigin.infrastructure.jpa.NewsOriginJpaRepository;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@Slf4j
@ActiveProfiles("test")
@SpringBootTest
class NewsOriginServiceImplTest {

  @Autowired
  NewsOriginServiceImpl newsOriginServiceImpl;

  @Autowired
  NewsOriginJpaRepository newsOriginJpaRepository;

  @Value("${app.client.naver.collect-news.search-display}")
  private String searchDisplay;

  @Test
  @DisplayName("뉴스 정보 수집 테스트")
  void collectOriginNewsTest() {
    // 불필요한 API 호출을 막기 위한 주석처리
//    int collectCount = newsOriginServiceImpl.collectOriginNews();
//
//    Integer inputCount = Integer.valueOf(searchDisplay);
//
//    Assertions.assertEquals(inputCount, collectCount);
  }

  @Test
  @DisplayName("Json 생성 테스트")
  void mkJsonTest() throws JsonProcessingException {

    LocalDateTime dateTime = LocalDateTime.of(
        2025, 4, 7, 23, 33, 29, 368_024_800);
    SendNewOriginDto sendNewOriginDto = SendNewOriginDto.of(
        "제목",
        "www.test.com",
        dateTime,
        "너무 힘들다는 사실이 아닐 수 없습니다.");

    ObjectMapper objectMapper = new ObjectMapper();
    String result = objectMapper.writeValueAsString(sendNewOriginDto);

    String expect = "{\"title\":\"제목\",\"url\":\"www.test.com\",\"publishedDate\":\"2025-04-07T23:33:29.368024800\",\"body\":\"너무 힘들다는 사실이 아닐 수 없습니다.\"}";
    Assertions.assertEquals(expect, result);
  }

  @Test
  @DisplayName("테스트 토픽에 메세지 전송 테스트")
  void sendNewsOriginMessagesTest() {
    // given
    NewsOrigin newsOrigin01 = NewsOrigin.create(
        "유럽, 美 제치고 클라우드 네이티브 주도권 확보… 디지털 주권 시대 개...",
        "https://www.tokenpost.kr/news/tech/235105",
        LocalDateTime.now());
    NewsOrigin newsOrigin02 = NewsOrigin.create(
        "[비즈토크&lt;상&gt;] 막 오른 미국발 '관세전쟁'…현대차, 가격 인상 선 그은...",
        "https://news.tf.co.kr/read/economy/2194711.htm",
        LocalDateTime.now());

    // 불필요한 API 호출을 막기 위한 주석 처리
//    newsOrigins.add(newsOrigin01);
//    newsOrigins.add(newsOrigin02);
//    NewsOrigin saved01 = newsOriginJpaRepository.save(newsOrigin01);
//    NewsOrigin saved02 = newsOriginJpaRepository.save(newsOrigin02);
//
//    newsOriginServiceImpl.sendNewsOriginMessages();
//
//    Assertions.assertEquals(true,
//        newsOriginJpaRepository.findById(saved01.getId()).get().getIsSentToQueue());
//    ;
//    Assertions.assertEquals(true,
//        newsOriginJpaRepository.findById(saved02.getId()).get().getIsSentToQueue());
//    ;
  }
}