package com.newpick4u.client.client.application.exception;

import com.newpick4u.common.exception.CustomException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ClientException extends CustomException {

  public ClientException(HttpStatus httpStatus, int code, String message) {
    super(httpStatus, code, message);
  }

  public static class DuplicateEmailException extends ClientException {

    public DuplicateEmailException() {
      super(HttpStatus.CONFLICT, HttpStatus.CONFLICT.value(), "이미 사용중인 이메일입니다.");
    }
  }

  public static class NotFoundException extends ClientException {

    public NotFoundException() {
      super(HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.value(), "존재하지 않는 고객사 정보입니다.");
    }
  }


  public static class DuplicatePhoneNumberException extends ClientException {

    public DuplicatePhoneNumberException() {
      super(HttpStatus.CONFLICT, HttpStatus.CONFLICT.value(), "이미 사용중인 전화번호입니다.");
    }
  }
}
