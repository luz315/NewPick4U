package com.newpick4u.comment.comment.infrastructure.client;

import com.newpick4u.comment.comment.application.dto.GetUserResponseDto;
import com.newpick4u.comment.global.config.FeignClientConfig;
import com.newpick4u.common.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", configuration = {FeignClientConfig.class})
public interface UserFeignClient {

  @GetMapping("/internal/v1/users/{userId}")
  ResponseEntity<ApiResponse<GetUserResponseDto>> getUser(@PathVariable("userId") Long userId);
}
