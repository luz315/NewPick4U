package com.newpick4u.comment.comment.application.usecase;

import com.newpick4u.comment.comment.application.CommentSearchCriteria;
import com.newpick4u.comment.comment.application.dto.CommentListPageDto.CommentContentDto;
import com.newpick4u.comment.comment.application.dto.CommentSaveRequestDto;
import com.newpick4u.comment.comment.application.dto.CommentUpdateDto;
import com.newpick4u.comment.comment.application.dto.GetCommentListForThreadResponseDto;
import com.newpick4u.comment.comment.application.dto.GetCommentResponseDto;
import com.newpick4u.common.resolver.dto.CurrentUserInfoDto;
import com.newpick4u.common.response.PageResponse;
import java.util.Map;
import java.util.UUID;

public interface CommentService {

  Map<String, Object> saveCommentForNews(CommentSaveRequestDto saveDto,
      CurrentUserInfoDto currentUserInfo);

  UUID saveCommentForThread(CommentSaveRequestDto saveDto,
      CurrentUserInfoDto currentUserInfo);

  UUID updateComment(UUID commentId, CommentUpdateDto updateDto, CurrentUserInfoDto userInfoDto);

  Long createGood(UUID commentId, CurrentUserInfoDto currentUserInfoDto);

  Long deleteGood(UUID commentId, CurrentUserInfoDto currentUserInfoDto);

  GetCommentResponseDto getComment(UUID commentId, CurrentUserInfoDto currentUserInfoDto);

  PageResponse<CommentContentDto> getCommentList(
      CommentSearchCriteria commentSearchCriteria,
      CurrentUserInfoDto currentUserInfoDto);

  GetCommentListForThreadResponseDto getCommentByThreadId(UUID threadId);
}
