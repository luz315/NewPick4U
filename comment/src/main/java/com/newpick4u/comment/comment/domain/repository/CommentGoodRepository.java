package com.newpick4u.comment.comment.domain.repository;

import com.newpick4u.comment.comment.domain.entity.CommentGood;
import java.util.Optional;
import java.util.UUID;

public interface CommentGoodRepository {
  
  Optional<CommentGood> findByCommentIdAndUserId(UUID commentId, Long userId);
}
