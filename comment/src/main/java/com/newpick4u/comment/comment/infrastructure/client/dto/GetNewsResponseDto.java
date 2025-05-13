package com.newpick4u.comment.comment.infrastructure.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GetNewsResponseDto(
    UUID id
) {

  public boolean isExist() {
    return id != null;
  }
}
