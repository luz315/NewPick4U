package com.newpick4u.common.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;

import java.util.List;

public record ErrorResponse(
    int code,
    HttpStatus status,
    String message,
    @JsonInclude(JsonInclude.Include.NON_NULL) List<ErrorField> errors
) {

  public static ErrorResponse of(HttpStatus status, Throwable ex) {
    return new ErrorResponse(status.value(), status, ex.getMessage(), null);
  }

  public static ErrorResponse of(HttpStatus status, Exception ex) {
    return new ErrorResponse(status.value(), status, ex.getMessage(), null);
  }

  public static ErrorResponse of(HttpStatus status, String message, List<ErrorField> errors) {
    return new ErrorResponse(status.value(), status, message, errors);
  }

  public record ErrorField(Object value, String message) {

  }
}