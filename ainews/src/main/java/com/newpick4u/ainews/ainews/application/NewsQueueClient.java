package com.newpick4u.ainews.ainews.application;

public interface NewsQueueClient {

  void sendNews(String message);

  void sendNewsDLQ(String message);
}
