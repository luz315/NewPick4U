package com.newpick4u.comment.comment.application.usecase;

import com.newpick4u.comment.comment.application.dto.CommentSaveRequestDto;
import com.newpick4u.comment.comment.application.dto.CommentUpdateDto;
import com.newpick4u.common.resolver.dto.CurrentUserInfoDto;
import java.util.UUID;

public interface CommentService {

  // 댓글 저장 : 뉴스 댓글
  UUID saveCommentForNews(CommentSaveRequestDto saveDto,
      CurrentUserInfoDto currentUserInfo);

  // 댓글 저장 : 쓰레드
  UUID saveCommentForThread(CommentSaveRequestDto saveDto,
      CurrentUserInfoDto currentUserInfo);

  // 댓글 업데이트
  UUID updateComment(UUID commentId, CommentUpdateDto updateDto, CurrentUserInfoDto userInfoDto);

  Long createGood(UUID commentId, CurrentUserInfoDto currentUserInfoDto);

  Long deleteGood(UUID commentId, CurrentUserInfoDto currentUserInfoDto);
}
