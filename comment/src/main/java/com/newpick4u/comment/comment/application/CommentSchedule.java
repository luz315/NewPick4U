package com.newpick4u.comment.comment.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class CommentSchedule {

  private final TagCacheRepository tagCacheRepository;

  @Scheduled(fixedDelay = 5 * 60 * 1000)
  public void deleteTagScoreCacheByTTL() {
    log.info("Start Delete TagScore Cache Task ============");
    Long totalDeletedCount = tagCacheRepository.deleteTagScoreCacheByTTL();
    log.info("End Delete TagScore Cache Task : delete count = {} ============", totalDeletedCount);
  }
}
