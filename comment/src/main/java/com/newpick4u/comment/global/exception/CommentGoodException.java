package com.newpick4u.comment.global.exception;

import com.newpick4u.common.exception.CustomException;
import com.newpick4u.common.exception.type.ErrorCode;
import org.springframework.http.HttpStatus;

public class CommentGoodException extends CustomException {

  public CommentGoodException(ErrorCode errorCode) {
    super(errorCode);
  }

  public CommentGoodException(HttpStatus httpStatus, int code, String message) {
    super(httpStatus, code, message);
  }

  public static class CommentGoodAlreadyExistException extends CommentGoodException {

    public CommentGoodAlreadyExistException() {
      super(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.value(),
          "이미 좋아요 이력이 존재합니다.");
    }
  }

  public static class CommentGoodAlreadyDeletedException extends CommentGoodException {

    public CommentGoodAlreadyDeletedException() {
      super(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.value(),
          "이미 취소된 좋아요 요청입니다.");
    }
  }

  public static class PermissionDeniedException extends CommentException {

    public PermissionDeniedException() {
      super(HttpStatus.FORBIDDEN, HttpStatus.FORBIDDEN.value(),
          "권한이 없습니다.");
    }
  }
}
