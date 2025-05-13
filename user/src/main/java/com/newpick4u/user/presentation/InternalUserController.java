package com.newpick4u.user.presentation;

import com.newpick4u.common.response.ApiResponse;
import com.newpick4u.user.application.dto.response.GetUserResponseDto;
import com.newpick4u.user.application.usecase.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/v1/users")
public class InternalUserController {

  private final UserService userService;

  @GetMapping("/{userId}")
  public ResponseEntity<ApiResponse<GetUserResponseDto>> getUser(
      @PathVariable("userId") Long userId) {
    GetUserResponseDto response = userService.getUser(userId);
    return ResponseEntity.ok(ApiResponse.of(HttpStatus.OK, response));
  }

}
