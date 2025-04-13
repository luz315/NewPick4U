package com.newpick4u.thread.thread.application.dto;

import java.util.UUID;

public record CommentResponse(
    UUID threadId,
    String content
) {

}
