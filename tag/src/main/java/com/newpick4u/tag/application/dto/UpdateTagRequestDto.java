package com.newpick4u.tag.application.dto;

import com.newpick4u.tag.domain.entity.Tag;

public record UpdateTagRequestDto(
    String tagName
) {

  public Tag toEntity() {
    return Tag.of(tagName, null);
  }
}
