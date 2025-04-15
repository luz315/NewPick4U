package com.newpick4u.comment.comment.domain.repository;

import com.newpick4u.comment.comment.domain.entity.Comment;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CommentRepository {

  Comment save(Comment comment);

  Optional<Comment> findById(UUID commentId);

  List<Comment> findAllByThreadIdAndDeletedAtIsNull(UUID threadId);
}
