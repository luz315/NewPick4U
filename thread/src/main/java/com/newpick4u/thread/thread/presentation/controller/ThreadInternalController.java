package com.newpick4u.thread.thread.presentation.controller;

import com.newpick4u.thread.thread.application.usecase.ThreadService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/v1/threads")
public class ThreadInternalController {

  private final ThreadService threadService;

  @GetMapping("/{threadId}")
  private Boolean existThread(@PathVariable UUID threadId) {
    return threadService.existThread(threadId);
  }
}
