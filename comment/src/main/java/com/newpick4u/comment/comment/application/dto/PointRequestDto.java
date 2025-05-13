package com.newpick4u.comment.comment.application.dto;

import java.util.UUID;

public record PointRequestDto(
    UUID advertisementId,
    Long userId,
    UUID commentId
) {

  public static PointRequestDto of(UUID advertisementId, Long userId, UUID commentId) {
    return new PointRequestDto(advertisementId, userId, commentId);
  }
}
