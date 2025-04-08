package com.newpick4u.thread.thread.application.dto;

import com.newpick4u.thread.thread.domain.entity.Thread;
import java.util.UUID;

public record ThreadResponseDto(
    UUID threadId,
    UUID newsId,
    Long commentCount
) {

  public static ThreadResponseDto from(Thread thread, Long count) {
    return new ThreadResponseDto(thread.getId(), thread.getNewsId(), count);
  }

}
