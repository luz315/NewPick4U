package com.newpick4u.thread.thread.application.exception;

import com.newpick4u.common.exception.CustomException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ThreadException extends CustomException {

  public ThreadException(HttpStatus httpStatus, int code, String message) {
    super(httpStatus, code, message);
  }

  public static class NotFoundException extends ThreadException {

    public NotFoundException() {
      super(HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.value(), "존재하지 않는 쓰레드입니다.");
    }
  }
}
