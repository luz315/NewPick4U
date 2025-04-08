package com.newpick4u.thread.global.config;

import com.newpick4u.thread.thread.infrastructure.client.CommentClient;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class CommentClientFallback implements CommentClient {

  @Override
  public Map<UUID, Long> getCommentCountMap(List<UUID> threadIds) {
    return threadIds.stream().collect(Collectors.toMap(id -> id, id -> 0L));
  }
}
