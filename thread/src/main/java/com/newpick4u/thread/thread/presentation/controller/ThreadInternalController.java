package com.newpick4u.thread.thread.presentation.controller;

import com.newpick4u.common.response.ApiResponse;
import com.newpick4u.thread.thread.application.usecase.ThreadService;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
  private ResponseEntity<ApiResponse<Map<String, Boolean>>> existThread(
      @PathVariable UUID threadId) {
    Boolean isExist = threadService.existThread(threadId);

    return ResponseEntity
        .status(HttpStatus.OK.value())
        .body(
            ApiResponse.of(
                HttpStatus.OK,
                "Success",
                Map.of("isExist", isExist)
            )
        );
  }
}
