package com.newpick4u.ainews.ainews.application.dto;

import java.util.UUID;

public record NewsInfoDto(
    UUID aiNewsId,
    String title,
    String content,
    String url,
    String publishedDate
) {

  public static NewsInfoDto of(
      UUID aiNewsId,
      String title,
      String content,
      String url,
      String publishedDate
  ) {
    return new NewsInfoDto(aiNewsId, title, content, url, publishedDate);
  }

}
