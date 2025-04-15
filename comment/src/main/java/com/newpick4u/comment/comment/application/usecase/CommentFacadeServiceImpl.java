package com.newpick4u.comment.comment.application.usecase;

import com.newpick4u.comment.comment.infrastructure.distributionlock.DistributedLock;
import com.newpick4u.common.resolver.dto.CurrentUserInfoDto;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CommentFacadeServiceImpl implements CommentFacadeService {

  private final CommentService commentService;

  @DistributedLock(key = "#commentId")
  @Override
  public Long createGood(UUID commentId, CurrentUserInfoDto currentUserInfoDto) {
    return commentService.createGood(commentId, currentUserInfoDto);
  }

  @DistributedLock(key = "#commentId")
  @Override
  public Long deleteGood(UUID commentId, CurrentUserInfoDto currentUserInfoDto) {
    return commentService.deleteGood(commentId, currentUserInfoDto);
  }
}
