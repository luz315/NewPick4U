package com.newpick4u.user.presentation;

import com.newpick4u.common.response.ApiResponse;
import com.newpick4u.user.application.dto.request.CreateUserRequestDto;
import com.newpick4u.user.application.dto.request.SignInUserRequestDto;
import com.newpick4u.user.application.dto.response.SignInUserResponseDto;
import com.newpick4u.user.application.usecase.UserService;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserApiController {

  private final UserService userService;

  @PostMapping
  public ResponseEntity<ApiResponse<Map<String, Long>>> createUser(
      @RequestBody @Valid CreateUserRequestDto request) {
    Long userId = userService.createUser(request);
    return ResponseEntity.ok(ApiResponse.of(HttpStatus.CREATED, Map.of("userId", userId)));
  }

  @PostMapping("/signin")
  public ResponseEntity<ApiResponse<SignInUserResponseDto>> signInUser(
      @RequestBody @Valid SignInUserRequestDto request) {
    SignInUserResponseDto response = userService.signInUser(request);
    return ResponseEntity.ok(ApiResponse.of(HttpStatus.OK, response));
  }

}
