package com.newpick4u.comment.comment.infrastructure.jpa;

import com.newpick4u.comment.comment.domain.entity.Comment;
import com.newpick4u.comment.comment.domain.repository.CommentRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class CommentRepositoryImpl implements CommentRepository {

  private final CommentJpaRepository commentJpaRepository;
  private final CommentRepositoryCustom commentRepositoryCustom;

  @Override
  public Comment save(Comment comment) {
    return commentJpaRepository.save(comment);
  }

  @Override
  public Optional<Comment> findById(UUID commentId) {
    return commentJpaRepository.findById(commentId);
  }

  @Override
  public List<Comment> findAllByThreadIdAndDeletedAtIsNull(UUID threadId) {
    return commentJpaRepository.findAllByThreadIdAndDeletedAtIsNull(threadId);
  }

}
