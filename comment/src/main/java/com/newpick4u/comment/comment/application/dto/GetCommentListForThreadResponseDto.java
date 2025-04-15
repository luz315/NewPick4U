package com.newpick4u.comment.comment.application.dto;

import java.util.List;
import java.util.UUID;

public record GetCommentListForThreadResponseDto(
    UUID threadId,
    List<String> commentList
) {

  public static GetCommentListForThreadResponseDto of(List<String> contentList, UUID threadId) {
    return new GetCommentListForThreadResponseDto(threadId, contentList);
  }
}
