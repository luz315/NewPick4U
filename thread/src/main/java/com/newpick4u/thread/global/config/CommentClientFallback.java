package com.newpick4u.thread.global.config;

import com.newpick4u.thread.thread.infrastructure.client.CommentClient;
import com.newpick4u.thread.thread.infrastructure.client.dto.CommentResponse;
import java.util.List;
import java.util.UUID;

public class CommentClientFallback implements CommentClient {

  @Override
  public CommentResponse getAllByThreadId(UUID threadId) {
    return new CommentResponse(threadId, List.of());
  }
}
