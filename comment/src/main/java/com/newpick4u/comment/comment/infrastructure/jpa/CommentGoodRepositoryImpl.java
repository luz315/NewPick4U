package com.newpick4u.comment.comment.infrastructure.jpa;

import com.newpick4u.comment.comment.domain.entity.CommentGood;
import com.newpick4u.comment.comment.domain.repository.CommentGoodRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class CommentGoodRepositoryImpl implements CommentGoodRepository {

  private final CommentGoodJpaRepository commentGoodJpaRepository;

  @Override
  public Optional<CommentGood> findByCommentIdAndUserId(UUID commentId, Long userId) {
    return commentGoodJpaRepository.findByCommentIdAndUserId(commentId, userId);
  }
}
