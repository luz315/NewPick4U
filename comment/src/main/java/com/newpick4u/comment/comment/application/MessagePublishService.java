package com.newpick4u.comment.comment.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class MessagePublishService {

  private final List<EventPublisher> eventPublishers;

  public void sendMessage(String message, EventType eventType) {
    this.eventPublishers.stream()
        .filter(ep -> ep.isSupport(eventType))
        .findFirst()
        .orElseThrow(() -> new RuntimeException("Not Support EventType : " + eventType.name()))
        .sendMessage(message, eventType);
  }
}
