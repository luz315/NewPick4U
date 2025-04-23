package com.newpick4u.comment.comment.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record CommentWithGoodDto(
    UUID commentId,
    UUID newsId,
    UUID threadId,
    String content,
    Long goodCount,
    Long goodId,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {

}