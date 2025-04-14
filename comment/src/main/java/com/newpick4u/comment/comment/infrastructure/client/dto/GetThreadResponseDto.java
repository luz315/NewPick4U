package com.newpick4u.comment.comment.infrastructure.client.dto;

import java.util.UUID;

public record GetThreadResponseDto(
    UUID id
) {

  public boolean isExist() {
    return id != null;
  }
}
