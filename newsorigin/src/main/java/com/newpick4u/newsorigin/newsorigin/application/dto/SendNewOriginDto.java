package com.newpick4u.newsorigin.newsorigin.application.dto;

import java.time.LocalDateTime;

public record SendNewOriginDto(
    String title,
    String url,
    String publishedDate,
    String body
) {

  public static SendNewOriginDto of(
      String title,
      String url,
      LocalDateTime publishedDate,
      String body
  ) {

    return new SendNewOriginDto(title, url, publishedDate.toString(), body);
  }
}
