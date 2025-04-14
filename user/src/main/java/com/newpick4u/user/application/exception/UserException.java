package com.newpick4u.user.application.exception;

import com.newpick4u.common.exception.CustomException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class UserException extends CustomException {

  public UserException(HttpStatus httpStatus, int code, String message) {
    super(httpStatus, code, message);
  }

  public static class AlreadyExistsUserNameException extends UserException {

    public AlreadyExistsUserNameException() {
      super(HttpStatus.CONFLICT, HttpStatus.CONFLICT.value(), "이미 존재하는 아이디입니다.");
    }
  }

  public static class NotFoundException extends UserException {

    public NotFoundException() {
      super(HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.value(), "존재하지 않는 유저입니다.");
    }
  }

  public static class InvalidPasswordException extends UserException {

    public InvalidPasswordException() {
      super(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.value(), "비밀번호가 일치하지 않습니다.");
    }
  }


}
