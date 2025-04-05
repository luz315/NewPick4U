package com.newpick4u.common.exception.type;

import org.springframework.http.HttpStatus;

public interface ErrorCode {

  int getCode();

  String getMessage();

  HttpStatus getStatus();

  default int getHttpStatus() {
    return getStatus().value();
  }

}
