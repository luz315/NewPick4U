package com.newpick4u.thread.global.config;

import com.newpick4u.thread.thread.application.dto.CommentResponse;
import com.newpick4u.thread.thread.infrastructure.client.CommentClient;
import java.util.List;
import java.util.UUID;

public class CommentClientFallback implements CommentClient {

  @Override
  public List<CommentResponse> getAllByThreadId(UUID threadId) {
    return List.of();
  }
}
