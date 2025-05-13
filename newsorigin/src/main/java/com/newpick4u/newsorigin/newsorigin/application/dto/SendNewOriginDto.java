package com.newpick4u.newsorigin.newsorigin.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record SendNewOriginDto(
    String originNewsId,
    String title,
    String url,
    String publishedDate,
    String body
) {

  public static SendNewOriginDto of(
      UUID originNewsId,
      String title,
      String url,
      LocalDateTime publishedDate,
      String body
  ) {

    return new SendNewOriginDto(originNewsId.toString(), title, url, publishedDate.toString(),
        body);
  }
}
