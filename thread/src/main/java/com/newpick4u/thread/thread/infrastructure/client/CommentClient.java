package com.newpick4u.thread.thread.infrastructure.client;

import com.newpick4u.thread.global.config.CommentClientFallback;
import com.newpick4u.thread.global.config.FeignClientConfig;
import com.newpick4u.thread.thread.application.dto.CommentResponse;
import java.util.List;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "comment-service", configuration = FeignClientConfig.class, fallback = CommentClientFallback.class)
public interface CommentClient {

  @GetMapping("/api/internal/comments/threads/{threadId}")
  List<CommentResponse> getAllByThreadId(@PathVariable UUID threadId);

}
