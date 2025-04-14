package com.newpick4u.comment.comment.presentation;

import com.newpick4u.comment.comment.application.dto.GetCommentListForThreadResponseDto;
import com.newpick4u.comment.comment.application.usecase.CommentService;
import com.newpick4u.common.response.ApiResponse;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/internal/v1/comment")
@RequiredArgsConstructor
@RestController
public class CommentInternalController {

  private final CommentService commentService;
  
  @GetMapping("/thread/{threadId}")
  public ResponseEntity<ApiResponse<GetCommentListForThreadResponseDto>> getCommentById(
      @PathVariable("threadId") UUID threadId
  ) {
    GetCommentListForThreadResponseDto commentListByThreadId = commentService.getCommentByThreadId(
        threadId);

    return ResponseEntity
        .status(HttpStatus.CREATED.value())
        .body(
            ApiResponse.of(
                HttpStatus.OK,
                "Success",
                commentListByThreadId
            )
        );
  }
}
