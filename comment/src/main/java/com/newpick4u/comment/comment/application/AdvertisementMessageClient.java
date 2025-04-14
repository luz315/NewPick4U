package com.newpick4u.comment.comment.application;

public interface AdvertisementMessageClient {

  // 정상 케이스 전송
  void sendPointRequestMessage(String message);

  // 실패 케이스 전송
  void sendPointRequestDLQ(String message);
}
