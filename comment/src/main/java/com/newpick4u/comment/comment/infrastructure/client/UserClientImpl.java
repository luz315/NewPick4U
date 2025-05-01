package com.newpick4u.comment.comment.infrastructure.client;

import com.newpick4u.comment.comment.application.UserClient;
import com.newpick4u.comment.comment.application.dto.GetUserResponseDto;
import com.newpick4u.common.response.ApiResponse;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UserClientImpl implements UserClient {

  private final UserFeignClient userFeignClient;

  @Override
  public String getUsername(Long userId) {
    ResponseEntity<ApiResponse<GetUserResponseDto>> userResponse = userFeignClient.getUser(userId);

    GetUserResponseDto data = Objects.requireNonNull(userResponse.getBody()).data();
    return data.username();
  }
}
