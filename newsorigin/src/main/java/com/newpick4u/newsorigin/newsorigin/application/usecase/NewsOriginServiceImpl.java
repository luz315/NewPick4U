package com.newpick4u.newsorigin.newsorigin.application.usecase;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.newpick4u.newsorigin.newsorigin.application.GetOriginBodyClient;
import com.newpick4u.newsorigin.newsorigin.application.MessageClient;
import com.newpick4u.newsorigin.newsorigin.application.OriginCollectClient;
import com.newpick4u.newsorigin.newsorigin.application.dto.NewNewsOriginDto;
import com.newpick4u.newsorigin.newsorigin.application.dto.SendNewOriginDto;
import com.newpick4u.newsorigin.newsorigin.application.parser.BodyParser;
import com.newpick4u.newsorigin.newsorigin.domain.entity.NewsOrigin;
import com.newpick4u.newsorigin.newsorigin.domain.repository.NewsOriginRepository;
import io.micrometer.common.util.StringUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class NewsOriginServiceImpl implements NewsOriginService {

  private final OriginCollectClient originCollectClient;
  private final GetOriginBodyClient getOriginBodyClient;
  private final NewsOriginRepository newsOriginRepository;
  private final BodyParser bodyParser;
  private final MessageClient messageClient;
  private final ObjectMapper mapper = new ObjectMapper();

  // 뉴스 수집 및 저장
  @Override
  public int collectOriginNews() {
    int count = 0;
    try {
      ArrayList<NewNewsOriginDto> originNewsList = originCollectClient.getOriginNewsList();

      List<NewsOrigin> newsOriginList = originNewsList.stream()
          .map(dto -> NewsOrigin.create(dto.title(), dto.url(), dto.publishedDate()))
          .toList();

      // TODO : 중복 기사 제거 로직 추가 예정

      List<NewsOrigin> savedNewsOriginList = newsOriginRepository.saveAll(newsOriginList);
      count = savedNewsOriginList.size();
    } catch (JsonProcessingException e) {
      log.error("", e);
    }
    return count;
  }

  // 뉴스 조회 및 전송
  @Override
  public int sendNewsOriginMessages() {

    // RDB 조회
    List<NewsOrigin> beforeSentNewsOrigin = newsOriginRepository.getAllByBeforeSentQueue();

    ExecutorService threadPool = Executors.newFixedThreadPool(beforeSentNewsOrigin.size());
    ArrayList<CompletableFuture<Void>> sendTaskList = new ArrayList<>();

    AtomicInteger updateCount = new AtomicInteger(0);

    for (NewsOrigin newsOrigin : beforeSentNewsOrigin) {
      CompletableFuture<Void> sendTask = CompletableFuture.runAsync(() -> {

        // 뉴스 원본 기사 획득
        String originNewsBody = getOriginBodyClient.getOriginNewsBody(newsOrigin.getUrl());
        if (StringUtils.isEmpty(originNewsBody)) {
          return;
        }

        // 기사 본문 추출
        String extractedMainBody = bodyParser.extractMainBody(originNewsBody,
            newsOrigin.getTitle());
        SendNewOriginDto sendNewOriginDto = SendNewOriginDto.of(
            newsOrigin.getTitle(),
            newsOrigin.getUrl(),
            newsOrigin.getNewsPublishedDate(),
            extractedMainBody);

        String jsonMessage = null;
        try {
          jsonMessage = mapper.writeValueAsString(sendNewOriginDto);
        } catch (JsonProcessingException e) {
          log.error("", e);
          return;
        }

        // message send
        boolean isSuccess = messageClient.sendNewsOriginMessage(jsonMessage);
        if (isSuccess) {
          newsOrigin.sentToQueue();
          newsOriginRepository.save(newsOrigin); // update
          updateCount.incrementAndGet();
        }
      }, threadPool);

      sendTaskList.add(sendTask);
    }

    sendTaskList.forEach(CompletableFuture::join);
    return updateCount.get();
  }
}
