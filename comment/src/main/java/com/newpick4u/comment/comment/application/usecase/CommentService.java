package com.newpick4u.comment.comment.application.usecase;

import com.newpick4u.comment.comment.application.dto.CommentSaveRequestDto;
import com.newpick4u.comment.comment.application.dto.CommentUpdateDto;
import com.newpick4u.common.resolver.dto.CurrentUserInfoDto;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;

public interface CommentService {

  // 댓글 저장 : 뉴스 댓글
  @Transactional
  UUID saveCommentForNews(CommentSaveRequestDto saveDto,
      CurrentUserInfoDto currentUserInfo);

  // 댓글 저장 : 쓰레드
  @Transactional
  UUID saveCommentForThread(CommentSaveRequestDto saveDto,
      CurrentUserInfoDto currentUserInfo);

  // 댓글 업데이트
  @Transactional
  UUID updateComment(UUID commentId, CommentUpdateDto updateDto, CurrentUserInfoDto userInfoDto);
}
