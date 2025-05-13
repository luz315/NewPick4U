package com.newpick4u.comment.comment.domain.repository;

import com.newpick4u.comment.comment.application.CommentSearchCriteria;
import com.newpick4u.comment.comment.application.dto.CommentListPageDto.CommentContentDto;
import com.newpick4u.comment.comment.domain.entity.Comment;
import com.newpick4u.common.response.PageResponse;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CommentRepository {

  Comment save(Comment comment);

  Optional<Comment> findById(UUID commentId);

  List<Comment> findAllByThreadIdAndDeletedAtIsNull(UUID threadId);

  PageResponse<CommentContentDto> findCommentsWithUserGood(
      Long userId, CommentSearchCriteria commentSearchCriteria);
}
