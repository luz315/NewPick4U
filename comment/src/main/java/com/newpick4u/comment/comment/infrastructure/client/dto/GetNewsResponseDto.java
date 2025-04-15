package com.newpick4u.comment.comment.infrastructure.client.dto;

import java.util.UUID;

public record GetNewsResponseDto(
    UUID id
) {

  public boolean isExist() {
    return id != null;
  }
}
