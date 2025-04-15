package com.newpick4u.comment.comment.infrastructure.jpa;

import com.newpick4u.comment.comment.domain.entity.CommentGood;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentGoodJpaRepository extends JpaRepository<CommentGood, Long> {

  Optional<CommentGood> findByCommentIdAndUserId(UUID commentId, Long userId);
}
