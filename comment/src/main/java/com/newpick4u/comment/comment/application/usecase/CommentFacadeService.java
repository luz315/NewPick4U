package com.newpick4u.comment.comment.application.usecase;

import com.newpick4u.comment.comment.application.dto.CommentSaveRequestDto;
import com.newpick4u.common.resolver.dto.CurrentUserInfoDto;
import java.util.UUID;

public interface CommentFacadeService {

  Long createGood(UUID commentId, CurrentUserInfoDto currentUserInfoDto);

  Long deleteGood(UUID commentId, CurrentUserInfoDto currentUserInfoDto);

  UUID saveCommentForNews(CommentSaveRequestDto saveDto,
      CurrentUserInfoDto currentUserInfo);
}
