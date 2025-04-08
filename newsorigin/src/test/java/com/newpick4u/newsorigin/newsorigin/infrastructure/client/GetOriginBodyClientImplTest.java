package com.newpick4u.newsorigin.newsorigin.infrastructure.client;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
class GetOriginBodyClientImplTest {

  @Autowired
  GetOriginBodyClientImpl getOriginBodyClient;

  @Test
  @DisplayName("신문 원문 획득 정상호출 테스트")
  void getOriginNewsBodyTest() {
//    String targetUrl = "http://www.g-enews.com/ko-kr/news/article/news_all/2025040519133638279a1f309431_1/article.html";
//    getOriginBodyClient.getOriginBody(targetUrl);
  }
}