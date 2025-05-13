package com.newpick4u.user.application.dto.response;

public record SignInUserResponseDto(String accessToken, Long userId) {

  public static SignInUserResponseDto of(String accessToken, Long userId) {
    return new SignInUserResponseDto(accessToken, userId);
  }
}
