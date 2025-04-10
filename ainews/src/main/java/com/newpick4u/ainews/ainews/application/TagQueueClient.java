package com.newpick4u.ainews.ainews.application;

public interface TagQueueClient {

  // 정상 케이스 전송
  void sendTag(String message);

  // 실패 케이스 전송
  void sendTagDLQ(String message);
}
