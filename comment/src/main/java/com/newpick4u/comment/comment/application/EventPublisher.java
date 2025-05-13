package com.newpick4u.comment.comment.application;

public interface EventPublisher {

  boolean isSupport(EventType eventType);

  void sendMessage(String message, EventType eventType);
}
