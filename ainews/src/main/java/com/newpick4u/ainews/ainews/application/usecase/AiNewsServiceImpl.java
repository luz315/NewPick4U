package com.newpick4u.ainews.ainews.application.usecase;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.newpick4u.ainews.ainews.application.AiClient;
import com.newpick4u.ainews.ainews.application.AiQueueClient;
import com.newpick4u.ainews.ainews.application.NewsQueueClient;
import com.newpick4u.ainews.ainews.application.TagQueueClient;
import com.newpick4u.ainews.ainews.application.dto.NewsInfoDto;
import com.newpick4u.ainews.ainews.application.dto.NewsOriginDto;
import com.newpick4u.ainews.ainews.application.dto.ProceedAiNewsDto;
import com.newpick4u.ainews.ainews.application.dto.TagInfoDto;
import com.newpick4u.ainews.ainews.domain.entity.AiNews;
import com.newpick4u.ainews.ainews.domain.repository.AiNewsRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class AiNewsServiceImpl implements AiNewsService {

  private final AiClient aiClient;
  private final AiNewsRepository aiNewsRepository;
  private final TagQueueClient tagQueueClient;
  private final NewsQueueClient newsQueueClient;
  private final AiQueueClient aiQueueClient;
  private final ObjectMapper objectMapper;

  @Override
  public void processAiNews(String originalMessage) {
    try {
      NewsOriginDto newsOriginDto = objectMapper.readValue(originalMessage, NewsOriginDto.class);
      ProceedAiNewsDto proceedAiNewsDto = getProceedAiNewsFromApi(originalMessage, newsOriginDto);
      if (proceedAiNewsDto == null) {
        aiQueueClient.sendApiCallFailDLQ(originalMessage);
        return;
      }

      saveAndSendTask(newsOriginDto, proceedAiNewsDto);

    } catch (JsonProcessingException jpe) {
      // json 관련 예외는 재처리 안함
      log.error("Json Processing Exception -> Never Retry : target={}", originalMessage, jpe);
    }
  }

  private void saveAndSendTask(NewsOriginDto newsOriginDto, ProceedAiNewsDto proceedAiNewsDto)
      throws JsonProcessingException {

    AiNews aiNews = createAiNewsByDto(newsOriginDto, proceedAiNewsDto);

    startProcess(aiNews, proceedAiNewsDto.proceedFields().tags());
  }

  @Override
  public void saveAndSendTaskByListener(String aiNewsString)
      throws JsonProcessingException {

    AiNews aiNews = createAiNewsByJsonString(aiNewsString);

    startProcess(aiNews, aiNews.getTagList());
  }

  private void startProcess(AiNews aiNews, List<String> tags) throws JsonProcessingException {
    // DB Save
    AiNews savedAiNews = saveAiNews(aiNews);
    if (savedAiNews == null) {
      return;
    }

    // send -> Tag Service
    sendTagMessage(savedAiNews.getId(), tags);

    // send -> News Service
    sendNewsMessage(savedAiNews);
  }

  private AiNews saveAiNews(AiNews aiNews)
      throws JsonProcessingException {
    AiNews savedAiNews;
    try {
      savedAiNews = aiNewsRepository.save(aiNews);
    } catch (Exception e) {
      String aiNewsJson = objectMapper.writeValueAsString(aiNews);
      log.error("Save Fail -> Send DLQ : {}", aiNewsJson, e);
      aiQueueClient.saveDBFailDLQ(aiNewsJson);
      return null;
    }
    return savedAiNews;
  }

  private void sendTagMessage(UUID aiNewsId, List<String> tags) throws JsonProcessingException {
    TagInfoDto tagInfoDto = TagInfoDto.of(tags, aiNewsId);
    String tagsJson = objectMapper.writeValueAsString(tagInfoDto);
    tagQueueClient.sendTag(tagsJson);
  }

  private void sendNewsMessage(AiNews aiNews) throws JsonProcessingException {
    NewsInfoDto newsInfoDto = NewsInfoDto.of(
        aiNews.getId(),
        aiNews.getTitle(),
        aiNews.getSummary(),
        aiNews.getUrl(),
        aiNews.getPublishedDate()
    );

    String newsInfoJson = objectMapper.writeValueAsString(newsInfoDto);
    newsQueueClient.sendNews(newsInfoJson);
  }

  private ProceedAiNewsDto getProceedAiNewsFromApi(String originalMessage,
      NewsOriginDto newsOriginDto)
      throws JsonProcessingException {
    ProceedAiNewsDto proceedAiNewsDto;
    try {
      proceedAiNewsDto = aiClient.processByAiApi(newsOriginDto.body());
    } catch (JsonProcessingException jpe) {
      throw jpe;
    } catch (Exception e) {
      aiQueueClient.sendApiCallFailDLQ(originalMessage);
      return null;
    }
    return proceedAiNewsDto;
  }

  private AiNews createAiNewsByDto(NewsOriginDto newsOriginDto,
      ProceedAiNewsDto proceedAiNewsDto) {
    AiNews aiNews = AiNews.create(
        UUID.fromString(newsOriginDto.originNewsId()),
        newsOriginDto.url(),
        newsOriginDto.title(),
        proceedAiNewsDto.proceedFields().getTagsString(),
        proceedAiNewsDto.proceedFields().summary(),
        newsOriginDto.publishedDate(),
        proceedAiNewsDto.originalString()
    );
    return aiNews;
  }

  private AiNews createAiNewsByJsonString(String aiNewsString)
      throws JsonProcessingException {
    AiNews aiNews = objectMapper.readValue(aiNewsString, AiNews.class);
    return aiNews;
  }

}
