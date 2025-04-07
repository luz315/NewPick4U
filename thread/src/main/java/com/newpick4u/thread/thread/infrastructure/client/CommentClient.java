package com.newpick4u.thread.thread.infrastructure.client;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "comment-service")
public interface CommentClient {

  @GetMapping("/api/internal/comments/count/threads/{threadId}")
  Map<UUID, Long> getCommentCountMap(List<UUID> threadIds);
}
