package com.newpick4u.thread.thread.infrastructure.client.dto;

import java.util.List;
import java.util.UUID;

public record CommentResponse(
    UUID threadId,
    List<String> commentList
) {

}
