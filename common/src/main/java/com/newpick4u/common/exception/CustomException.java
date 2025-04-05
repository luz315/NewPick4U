package com.newpick4u.common.exception;

import com.newpick4u.common.exception.type.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomException extends RuntimeException {

  private final int code;
  private final HttpStatus httpStatus;

  public CustomException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.httpStatus = errorCode.getStatus();
    this.code = errorCode.getCode();
  }

  public CustomException(HttpStatus httpStatus, int code, String message) {
    super(message);
    this.httpStatus = httpStatus;
    this.code = code;
  }

  public static CustomException from(ErrorCode errorCode) {
    return new CustomException(errorCode);
  }

  public HttpStatus getStatus() {
    return this.httpStatus;
  }
}
