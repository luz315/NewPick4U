package com.newpick4u.client.advertisement.application.message.request;

import java.util.UUID;

public record PointUpdateMessage(Long userId, Integer point, UUID advertisementId) {

  public static PointUpdateMessage of(Long userId, Integer point, UUID advertisementId) {
    return new PointUpdateMessage(userId, point, advertisementId);
  }

}
