package com.newpick4u.client.advertisement.infrastructure.kafka;

public record PointUpdateMessage(Long userId, Integer point) {

  public static PointUpdateMessage of(Long userId, Integer point) {
    return new PointUpdateMessage(userId, point);
  }

}
