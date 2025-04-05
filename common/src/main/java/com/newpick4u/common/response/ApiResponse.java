package com.newpick4u.common.response;

import org.springframework.http.HttpStatus;

public record ApiResponse<T>(String message, int code, T data) {
  public static <T> ApiResponse<T> of(HttpStatus httpStatus, String message, T data) {
    return new ApiResponse<>(message, httpStatus.value(), data);
  }

  public static <T> ApiResponse<T> of(HttpStatus httpStatus, T data) {
    return of(httpStatus, httpStatus.name(), data);
  }

  public static <T> ApiResponse<T> ok(T data) {
    return of(HttpStatus.OK, data);
  }
}