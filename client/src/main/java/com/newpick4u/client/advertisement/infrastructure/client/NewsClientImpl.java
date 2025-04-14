package com.newpick4u.client.advertisement.infrastructure.client;

import com.newpick4u.client.advertisement.application.client.NewsClient;
import com.newpick4u.client.advertisement.application.dto.response.GetNewsResponseDto;
import com.newpick4u.client.global.config.FeignConfig;
import com.newpick4u.common.response.ApiResponse;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "news-service", configuration = FeignConfig.class)
public interface NewsClientImpl extends NewsClient {

  @GetMapping("internal/v1/news/{newsId}")
  ResponseEntity<ApiResponse<GetNewsResponseDto>> getNews(@PathVariable("newsId") UUID newsId);
}
