package com.newpick4u.ainews.ainews.application.usecase;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface AiNewsService {

  // 저장
  void processAiNews(String originalMessage);

  void saveAndSendTaskByListener(String aiNewsString) throws JsonProcessingException;
}
