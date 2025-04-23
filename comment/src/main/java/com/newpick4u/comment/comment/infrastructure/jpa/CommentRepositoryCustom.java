package com.newpick4u.comment.comment.infrastructure.jpa;

import com.newpick4u.comment.comment.application.dto.CommentWithGoodDto;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentRepositoryCustom {

  Page<CommentWithGoodDto> findCommentsWithUserGood(
      UUID newsId, UUID threadId, Long userId, Pageable pageable);
}
