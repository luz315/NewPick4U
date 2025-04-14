package com.newpick4u.comment.comment.infrastructure.client;

import com.newpick4u.comment.comment.infrastructure.client.dto.GetNewsResponseDto;
import com.newpick4u.comment.global.config.FeignClientConfig;
import com.newpick4u.common.response.ApiResponse;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "news-service", configuration = {FeignClientConfig.class})
public interface NewsFeignClient {

  @GetMapping("/internal/v1/news/{newsId}")
  ResponseEntity<ApiResponse<GetNewsResponseDto>> getNewsById(
      @PathVariable("newsId") UUID newsId);
}
