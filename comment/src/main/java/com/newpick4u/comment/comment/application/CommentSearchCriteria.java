package com.newpick4u.comment.comment.application;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

public record CommentSearchCriteria(
    UUID newsId,
    UUID threadId,
    Sort sort,
    Direction direction,
    int page,
    int size
) {

  @Getter
  @AllArgsConstructor
  public enum Sort {
    CREATEDAT("createdAt"),
    GOODCOUNT("goodCount");

    private final String fieldName;
  }

  public enum Direction {
    DESC,
    ASC
  }

}
