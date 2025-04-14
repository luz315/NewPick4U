package com.newpick4u.ainews.ainews.application;

public interface AiQueueClient {

  // 실패 케이스 전송 : API 호출 실패 케이스
  void sendApiCallFailDLQ(String message);

  // 실패 케이스 전송 : DB 저장 실패 케이스
  void saveDBFailDLQ(String message);
}
