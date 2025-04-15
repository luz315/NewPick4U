package com.newpick4u.comment.global.exception;

import com.newpick4u.common.exception.CustomException;
import com.newpick4u.common.exception.type.ErrorCode;
import org.springframework.http.HttpStatus;

public class CommentException extends CustomException {

  public CommentException(ErrorCode errorCode) {
    super(errorCode);
  }

  public CommentException(HttpStatus httpStatus, int code, String message) {
    super(httpStatus, code, message);
  }

  public static class ProcessFailException extends CommentException {

    public ProcessFailException() {
      super(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value(),
          "요청 처리에 실패하였습니다. 잠시 후 다시 시도해주세요.");
    }
  }

  public static class CommentNotFoundException extends CommentException {

    public CommentNotFoundException() {
      super(HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.value(),
          "댓글 정보가 존재하지 않습니다.");
    }
  }

  public static class PermissionDeniedException extends CommentException {

    public PermissionDeniedException() {
      super(HttpStatus.FORBIDDEN, HttpStatus.FORBIDDEN.value(),
          "권한이 없습니다.");
    }
  }

  public static class ConvertMessageFailException extends CommentException {

    public ConvertMessageFailException() {
      super(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value(),
          "메세지 구성에 실패하였습니다.");
    }
  }

  public static class NewsNotFoundException extends CommentException {

    public NewsNotFoundException() {
      super(HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.value(),
          "뉴스 정보가 존재하지 않습니다.");
    }
  }

  public static class ThreadNotFoundException extends CommentException {

    public ThreadNotFoundException() {
      super(HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.value(),
          "쓰레드 정보가 존재하지 않습니다.");
    }
  }

}
