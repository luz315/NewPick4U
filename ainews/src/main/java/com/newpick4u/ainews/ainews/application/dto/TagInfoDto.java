package com.newpick4u.ainews.ainews.application.dto;

import java.util.List;
import java.util.UUID;

public record TagInfoDto(
    List<String> tags,
    UUID aiNewsId
) {

  public static TagInfoDto of(List<String> tags, UUID aiNewsId) {
    return new TagInfoDto(tags, aiNewsId);
  }

}
