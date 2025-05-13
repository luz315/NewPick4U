package com.newpick4u.comment.comment.infrastructure.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GetThreadResponseDto(
    Boolean isExist
) {

  public Boolean isExist() {
    if (isExist == null) {
      return false;
    }
    
    return isExist;
  }
}
