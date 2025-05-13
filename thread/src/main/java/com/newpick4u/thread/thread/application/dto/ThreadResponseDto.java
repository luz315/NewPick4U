package com.newpick4u.thread.thread.application.dto;

import com.newpick4u.thread.thread.domain.entity.Thread;
import java.util.UUID;

public record ThreadResponseDto(
    UUID threadId,
    String tagName
) {

  public static ThreadResponseDto from(Thread thread) {
    return new ThreadResponseDto(thread.getId(), thread.getTagName());
  }

}
