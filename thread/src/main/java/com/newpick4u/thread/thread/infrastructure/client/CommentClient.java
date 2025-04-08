package com.newpick4u.thread.thread.infrastructure.client;

import com.newpick4u.thread.global.config.CommentClientFallback;
import com.newpick4u.thread.global.config.FeignClientConfig;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "comment-service", configuration = FeignClientConfig.class, fallback = CommentClientFallback.class)
public interface CommentClient {

  @GetMapping("/api/internal/comments/count/threads/{threadId}")
  Map<UUID, Long> getCommentCountMap(List<UUID> threadIds);
}
