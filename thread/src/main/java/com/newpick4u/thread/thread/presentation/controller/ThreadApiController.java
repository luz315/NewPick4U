package com.newpick4u.thread.thread.presentation.controller;

import com.newpick4u.common.response.ApiResponse;
import com.newpick4u.common.response.PageResponse;
import com.newpick4u.thread.thread.application.dto.ThreadDetailResponseDto;
import com.newpick4u.thread.thread.application.dto.ThreadResponseDto;
import com.newpick4u.thread.thread.application.usecase.ThreadService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/threads")
public class ThreadApiController {

  private final ThreadService threadService;

  @GetMapping()
  public ResponseEntity<PageResponse<ThreadResponseDto>> getThreads(
      @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

    Page<ThreadResponseDto> response = threadService.getThreads(pageable);
    return ResponseEntity.ok(PageResponse.from(response));

  }

  @GetMapping("/{threadId}")
  public ResponseEntity<ApiResponse<ThreadDetailResponseDto>> getThreadDetail(
      @PathVariable UUID threadId) {

    return ResponseEntity.ok(ApiResponse.of(HttpStatus.OK,
        ThreadDetailResponseDto.from(threadService.getThreadDetail(threadId))));
  }
}
