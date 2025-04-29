package com.newpick4u.thread.thread.presentation.controller;

import com.newpick4u.common.response.ApiResponse;
import com.newpick4u.common.response.SliceResponse;
import com.newpick4u.thread.thread.application.dto.ThreadDetailResponseDto;
import com.newpick4u.thread.thread.application.dto.ThreadResponseDto;
import com.newpick4u.thread.thread.application.usecase.ThreadService;
import io.micrometer.core.instrument.MeterRegistry;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/threads")
public class ThreadApiController {

  private final MeterRegistry meterRegistry;
  private final ThreadService threadService;

  @GetMapping()
  public ResponseEntity<ApiResponse<SliceResponse<ThreadResponseDto>>> getThreads(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size
  ) {
    final String getThreadRequestMetric = "threads_request";
    meterRegistry.counter(getThreadRequestMetric).increment();

    Pageable pageable = PageRequest.of(page, size);
    SliceResponse<ThreadResponseDto> response = threadService.getThreads(pageable);
    return ResponseEntity.status(HttpStatus.OK.value())
        .body(ApiResponse.of(HttpStatus.OK, response));
  }

  @GetMapping("/{threadId}")
  public ResponseEntity<ApiResponse<ThreadDetailResponseDto>> getThreadDetail(
      @PathVariable UUID threadId) {

    return ResponseEntity.ok(ApiResponse.of(HttpStatus.OK,
        ThreadDetailResponseDto.from(threadService.getThreadDetail(threadId))));
  }
}
