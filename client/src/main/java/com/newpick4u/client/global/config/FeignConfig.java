package com.newpick4u.client.global.config;

import com.newpick4u.client.global.interceptor.CustomHeaderInterceptor;
import feign.Request;
import feign.Retryer;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

  @Bean
  public CustomHeaderInterceptor customHeaderInterceptor() {
    return new CustomHeaderInterceptor();
  }

  @Bean
  public Request.Options requestOptions() {
    return new Request.Options(
        Duration.ofSeconds(5),
        Duration.ofSeconds(20),
        false
    );
  }

  @Bean
  Retryer.Default retryOptions() {
    // 0.1초의 간격으로 시작해 최대 3초의 간격으로 점점 증가하며, 최대 3회 재시도한다.
    return new Retryer.Default(100L, TimeUnit.SECONDS.toMillis(3L), 2);
  }


}
