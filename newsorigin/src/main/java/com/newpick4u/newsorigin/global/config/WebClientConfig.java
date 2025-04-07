package com.newpick4u.newsorigin.global.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.LoggingCodecSupport;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;
import reactor.netty.http.client.HttpClient;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class WebClientConfig {

  private static final int MB = 1024 * 1024; // MB 의 byte 단위
  @Value("${app.client.webclient.connection-timeout}")
  private int connectionTimeout;
  @Value("${app.client.webclient.read-timeout}")
  private int readTimeout;
  @Value("${app.client.webclient.max-buffer-size-MB}")
  private int maxBufferSizeMB;

  @Bean(name = "webClient")
  public WebClient webClient() {

    // 인코딩 옵션 제거
    DefaultUriBuilderFactory uriBuilderFactory = new DefaultUriBuilderFactory();
    uriBuilderFactory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.NONE);

    // 버퍼 사이즈 설정 : Request Data를 버퍼링하기 위한 메모리의 기본값 설정 (디폴트는 256KB)
    ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder()
        .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(maxBufferSizeMB * MB))
        .build();
    // 요청, 응답 확인을 위한 로그 설정 : 프로퍼티의 logging 레벨 옵션과 함께 적용 가능
    exchangeStrategies.messageWriters().stream()
        .filter(LoggingCodecSupport.class::isInstance)
        .forEach(writer -> ((LoggingCodecSupport) writer).setEnableLoggingRequestDetails(true));

    // client connect 설정 구성 (reactor netty )
    ReactorClientHttpConnector clientHttpConnector = new ReactorClientHttpConnector(
        HttpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectionTimeout)
            .doOnConnected(connection -> connection.addHandlerLast(
                new ReadTimeoutHandler(readTimeout, TimeUnit.MILLISECONDS)
            ))
    );

    return WebClient.builder()
        .uriBuilderFactory(uriBuilderFactory)
        .clientConnector(clientHttpConnector)
        .exchangeStrategies(exchangeStrategies)
        .build();
  }
}
