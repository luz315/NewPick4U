package com.newpick4u.comment.comment.infrastructure.client;

import com.newpick4u.comment.comment.infrastructure.client.dto.GetThreadResponseDto;
import com.newpick4u.comment.global.config.FeignClientConfig;
import com.newpick4u.common.response.ApiResponse;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "thread-service", configuration = {FeignClientConfig.class})
public interface ThreadFeignClient {

  @GetMapping("/internal/v1/thread/{threadId}")
  ResponseEntity<ApiResponse<GetThreadResponseDto>> getThreadById(
      @PathVariable("threadId") UUID newsId);
}
