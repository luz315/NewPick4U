package com.newpick4u.comment.global.config;

import feign.Request;
import feign.RequestInterceptor;
import feign.Retryer;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class FeignClientConfig {

  private static final String USER_ID_HEADER_NAME = "X-User-Id";
  private static final String USER_ROLE_HEADER_NAME = "X-User-Role";

  @Bean
  public RequestInterceptor requestInterceptor() {
    return requestTemplate -> {
      ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
      if (null != attributes) {
        HttpServletRequest request = attributes.getRequest();
        String userId = request.getHeader(USER_ID_HEADER_NAME);
        String role = request.getHeader(USER_ROLE_HEADER_NAME);
        if (userId != null) {
          requestTemplate.header(USER_ID_HEADER_NAME, userId);
        }
        if (role != null) {
          requestTemplate.header(USER_ROLE_HEADER_NAME, role);
        }
      }
    };
  }

  @Bean
  public Request.Options requestOptions() {
    return new Request.Options(
        Duration.ofSeconds(5),       // connectTimeOut
        Duration.ofSeconds(20),      // readTimeOut
        true                         // 리다이렉트 허용 여부
    );
  }

  // 재시도 옵션 : Feign이 제공하는 Retryer는 IOException이 발생한 경우에만 처리
  @Bean
  Retryer.Default retryOptions() {
    // 0.1초의 간격으로 시작해 최대 3초의 간격으로 점점 증가하며, 최대 3회 재시도한다.
    return new Retryer.Default(100L, TimeUnit.SECONDS.toMillis(3L), 2);
  }

}
