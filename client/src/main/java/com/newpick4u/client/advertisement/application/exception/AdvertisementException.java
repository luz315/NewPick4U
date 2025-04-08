package com.newpick4u.client.advertisement.application.exception;

import com.newpick4u.common.exception.CustomException;
import org.springframework.http.HttpStatus;

public class AdvertisementException extends CustomException {

  public AdvertisementException(HttpStatus httpStatus, int code, String message) {
    super(httpStatus, code, message);
  }

  public static class NewsNotFoundException extends AdvertisementException {

    public NewsNotFoundException() {
      super(HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.value(),
          "뉴스 정보가 존재하지 않아 광고 등록이 불가합니다.");
    }
  }

  public static class AlreadyExistsTitleOrUrl extends AdvertisementException {

    public AlreadyExistsTitleOrUrl() {
      super(HttpStatus.CONFLICT, HttpStatus.CONFLICT.value(),
          "등록하려는 광고의 제목 또는 url이 중복됩니다.");
    }
  }
}
