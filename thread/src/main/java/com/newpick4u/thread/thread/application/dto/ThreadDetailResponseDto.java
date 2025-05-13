package com.newpick4u.thread.thread.application.dto;

import com.newpick4u.thread.thread.domain.entity.Thread;
import java.util.UUID;

public record ThreadDetailResponseDto(
    UUID threadId,
    String tagName,
    String summary
) {

  public static ThreadDetailResponseDto from(Thread thread) {
    return new ThreadDetailResponseDto(thread.getId(), thread.getTagName(), thread.getSummary());
  }
}
