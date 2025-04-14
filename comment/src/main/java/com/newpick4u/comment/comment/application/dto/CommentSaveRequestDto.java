package com.newpick4u.comment.comment.application.dto;

import java.util.List;
import java.util.UUID;

public record CommentSaveRequestDto(
    Boolean isAdSet,
    UUID advertisementId,
    UUID threadId,
    UUID newsId,
    String content,
    List<String> newsTags
) {

  public boolean isHasParent() {
    if (isNewsComment()) {
      return true;
    }
    if (isThreadComment()) {
      return true;
    }

    return false;
  }

  public boolean isNewsComment() {
    return this.newsId != null;
  }

  public boolean isThreadComment() {
    return this.threadId != null;
  }
}
