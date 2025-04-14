package com.newpick4u.comment.comment.infrastructure.jpa;

import com.newpick4u.comment.comment.domain.entity.Comment;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentJpaRepository extends JpaRepository<Comment, UUID> {

  List<Comment> findAllByThreadIdAndDeletedAtIsNull(UUID threadId);
}
