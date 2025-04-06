package com.newpick4u.tag.application.dto;

import com.newpick4u.tag.domain.entity.Tag;

public record TagListResponseDto(
    String tagName,
    Long score
) {

  public static TagListResponseDto from(Tag tag) {
    return new TagListResponseDto(
        tag.getTagName(),
        tag.getScore()
    );
  }
}
