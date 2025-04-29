package com.newpick4u.comment.comment.application.dto;

import java.util.List;
import java.util.UUID;

public record CommentListPageDto(
    long totalElements,
    int totalPages,
    int pageNumber,
    int pageSize,
    List<CommentContentDto> commentList
) {

  public record CommentContentDto(
      UUID commentId,
      UUID newsId,
      UUID threadId,
      String content,
      Long goodCount,
      Boolean isCheckedGood,
      String createdAt,
      String updatedAt
  ) {

  }
}
