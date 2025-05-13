package com.newpick4u.newsorigin.newsorigin.application;

import com.newpick4u.newsorigin.newsorigin.application.usecase.NewsOriginService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NewsOriginSchedule {

  private final NewsOriginService newsOriginService;

  @Scheduled(cron = "2 0 * * * *")
  public void originNewsCollectSchedule() {
    log.info("Start OriginNews Collect =========================");
    int collectCount = newsOriginService.collectOriginNews();
    log.info("End OriginNews Collect : collectCount = {} =====", collectCount);
  }

  @Scheduled(cron = "2 * * * * *")
  public void sendNewsOriginMessagesSchedule() {
    log.info("Start Send News Origin Task =========================");
    int updateCount = newsOriginService.sendNewsOriginMessages();
    log.info("End Send News Origin Task : updateCount = {} =====", updateCount);
  }
}
