package com.newpick4u.newsorigin.newsorigin.infrastructure.client;

import com.newpick4u.newsorigin.newsorigin.application.GetOriginBodyClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@Component
public class GetOriginBodyClientImpl implements GetOriginBodyClient {

  private final WebClient webClient;

  public String getOriginNewsBody(String targetUrl) {
    String getBodyResult = webClient.get()
        .uri(targetUrl)
        .retrieve()
        .bodyToMono(String.class)
        .onErrorResume(throwable -> {
          log.error("Fail : url = {}", targetUrl, throwable);
          return Mono.empty();
        })
        .block();
    return getBodyResult;
  }

}
