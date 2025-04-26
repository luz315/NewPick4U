package com.newpick4u.comment.comment.application.dto;

import com.newpick4u.comment.comment.domain.entity.Comment;
import com.newpick4u.comment.global.common.CommonUtil;
import java.util.UUID;

public record GetCommentResponseDto(
    UUID commentId,
    UUID newsId,
    UUID threadId,
    String content,
    Long goodCount,
    Boolean isCheckedGood,
    String createdAt,
    String updatedAt
) {

  public static GetCommentResponseDto of(Comment comment, boolean isCommentGoodExist) {
    return new GetCommentResponseDto(
        comment.getId(),
        comment.getNewsId(),
        comment.getThreadId(),
        comment.getContent(),
        comment.getGoodCount(),
        isCommentGoodExist,
        CommonUtil.parseLDTToString(comment.getCreatedAt()),
        CommonUtil.parseLDTToString(comment.getUpdatedAt())
    );
  }

}
