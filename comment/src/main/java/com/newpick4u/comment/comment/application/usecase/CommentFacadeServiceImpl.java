package com.newpick4u.comment.comment.application.usecase;

import com.newpick4u.comment.comment.application.EventType;
import com.newpick4u.comment.comment.application.MessagePublishService;
import com.newpick4u.comment.comment.application.dto.CommentSaveRequestDto;
import com.newpick4u.comment.comment.infrastructure.distributionlock.DistributedLock;
import com.newpick4u.common.resolver.dto.CurrentUserInfoDto;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CommentFacadeServiceImpl implements CommentFacadeService {

  private final CommentService commentService;
  private final MessagePublishService messagePublishService;

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

  @Override
  public UUID saveCommentForNews(CommentSaveRequestDto saveDto,
      CurrentUserInfoDto currentUserInfo) {
    Map<String, Object> resultMap = commentService.saveCommentForNews(saveDto, currentUserInfo);
    if (saveDto.isAdSet()) {
      String eventMessage = (String) resultMap.get("eventMessage");
      try {
        messagePublishService.sendMessage(
            eventMessage,
            EventType.POINT_REQUEST_SEND
        );
      } catch (Exception e) {
        messagePublishService.sendMessage(
            eventMessage,
            EventType.FAIL_POINT_REQUEST
        );
      }
    }

    return (UUID) resultMap.get("commentId");
  }
}
