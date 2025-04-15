package com.newpick4u.gateway.global.exception;

import com.newpick4u.common.exception.type.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum GatewayErrorCode implements ErrorCode {

  // Unauthorized: 401 - 인증 이슈
  INVALID_JWT_SIGNATURE(HttpStatus.UNAUTHORIZED.value(), "유효하지 않은 JWT 서명입니다.",
      HttpStatus.UNAUTHORIZED),
  EXPIRED_JWT_TOKEN(HttpStatus.UNAUTHORIZED.value(), "만료된 JWT token 입니다.", HttpStatus.UNAUTHORIZED),
  UNSUPPORTED_JWT_TOKEN(HttpStatus.UNAUTHORIZED.value(), "지원되지 않는 JWT 토큰입니다.",
      HttpStatus.UNAUTHORIZED),
  JWT_CLAIMS_EMPTY(HttpStatus.UNAUTHORIZED.value(), "잘못된 JWT 토큰입니다.", HttpStatus.UNAUTHORIZED),

  // Not Found: 404 - 자원 없음
  USER_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "유저 개체를 찾지 못했습니다.", HttpStatus.NOT_FOUND),

  // Internal Server Error: 500 - 서버 문제 발생
  BAD_PADDING(HttpStatus.INTERNAL_SERVER_ERROR.value(), "잘못된 패딩입니다.",
      HttpStatus.INTERNAL_SERVER_ERROR);

  private final int code;
  private final String message;
  private final HttpStatus status;
}