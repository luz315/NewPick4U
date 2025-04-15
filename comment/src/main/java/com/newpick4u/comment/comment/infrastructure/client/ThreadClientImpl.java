package com.newpick4u.comment.comment.infrastructure.client;

import com.newpick4u.comment.comment.application.ThreadClient;
import com.newpick4u.comment.comment.infrastructure.client.dto.GetThreadResponseDto;
import com.newpick4u.common.response.ApiResponse;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ThreadClientImpl implements ThreadClient {

  private final ThreadFeignClient threadFeignClient;

  @Override
  public boolean isExistThread(UUID threadId) {
    ResponseEntity<ApiResponse<GetThreadResponseDto>> responseEntity = threadFeignClient.getThreadById(
        threadId);

    if (responseEntity.getStatusCode().is4xxClientError()) {
      return false;
    }

    GetThreadResponseDto data = Objects.requireNonNull(responseEntity.getBody()).data();
    return data.isExist();
  }
}
