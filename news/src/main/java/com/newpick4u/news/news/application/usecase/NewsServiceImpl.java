package com.newpick4u.news.news.application.usecase;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.newpick4u.common.resolver.dto.CurrentUserInfoDto;
import com.newpick4u.common.resolver.dto.UserRole;
import com.newpick4u.news.news.application.dto.NewsInfoDto;
import com.newpick4u.news.news.application.dto.NewsTagDto;
import com.newpick4u.news.news.application.dto.response.NewsResponseDto;
import com.newpick4u.news.news.application.dto.response.NewsSummaryDto;
import com.newpick4u.news.news.application.dto.response.PageResponse;
import com.newpick4u.news.news.domain.critria.NewsSearchCriteria;
import com.newpick4u.news.news.domain.entity.News;
import com.newpick4u.news.news.domain.entity.NewsTag;
import com.newpick4u.news.news.domain.entity.TagInbox;
import com.newpick4u.news.news.domain.model.Pagination;
import com.newpick4u.news.news.domain.repository.NewsRepository;
import com.newpick4u.news.news.domain.repository.TagInboxRepository;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class NewsServiceImpl implements NewsService {

  // 테스트용 시뮬레이션
  private static final Map<String, Integer> failureMap = new ConcurrentHashMap<>();
  private final NewsRepository newsRepository;
  private final TagInboxRepository tagInboxRepository;
  private final ObjectMapper objectMapper;

  private static void simulateFailures(String aiNewsId) {
    if ("fail-once".equals(aiNewsId)) {
      int count = failureMap.getOrDefault(aiNewsId, 0);
      log.info("[SimulateFail] 실행 카운트 - aiNewsId: {}, count: {}", aiNewsId, count);

      if (count < 1) {
        failureMap.put(aiNewsId, count + 1); // 첫 실패 기록
        log.warn("[SimulateFail] 첫 번째 실패 유도: {}", aiNewsId);

        throw new RuntimeException("첫 번째 실패 유도");
      }
    }
    if ("fail-me".equals(aiNewsId)) {
      throw new RuntimeException("무조건 실패 유도");
    }
  }

  @Transactional
  public void saveNewsInfo(NewsInfoDto dto) {
    simulateFailures(dto.aiNewsId()); // 테스트 조건 시뮬레이션

    if (newsRepository.existsByAiNewsId(dto.aiNewsId())) {
      throw new IllegalStateException("이미 저장된 뉴스입니다: " + dto.aiNewsId());
    }
    News news = News.create(dto.aiNewsId(), dto.title(), dto.content(), dto.url(),
        dto.publishedDate(), 0L);
    newsRepository.save(news);
  }

  @Transactional
  public void updateNewsTagList(NewsTagDto dto) {
    simulateFailures(dto.aiNewsId()); // 테스트 조건 시뮬레이션

    validateTagListSize(dto);
    newsRepository.findByAiNewsId(dto.aiNewsId())
        .ifPresentOrElse(
            news -> applyTagList(news, dto),
            () -> saveInbox(dto)
        );
  }

  // 내부 메서드
  private void validateTagListSize(NewsTagDto dto) {
    if (dto.tagList() == null || dto.tagList().size() > 10) {
      throw new IllegalArgumentException("뉴스 태그는 최대 10개까지 존재합니다.");
    }
  }

  private void applyTagList(News news, NewsTagDto dto) {
    List<NewsTag> tags = dto.tagList().stream()
        .map(tag -> NewsTag.create(tag.id(), tag.name(), news))
        .toList();
    news.addTags(tags);
  }

  private void saveInbox(NewsTagDto dto) {
    String json = serializeToJson(dto);
    TagInbox inbox = TagInbox.create(dto.aiNewsId(), json);
    tagInboxRepository.save(inbox);
  }

  private String serializeToJson(NewsTagDto dto) {
    try {
      return objectMapper.writeValueAsString(dto);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("태그 인박스 직렬화 실패", e);
    }
  }

  @Transactional(readOnly = true)
  public NewsResponseDto getNews(UUID id, CurrentUserInfoDto userInfoDto) {
    boolean isMaster = userInfoDto.role() == UserRole.ROLE_MASTER;
    News news = newsRepository.findNewsByRole(id, isMaster)
        .orElseThrow(() -> new IllegalArgumentException("뉴스를 찾을 수 없습니다."));
    return NewsResponseDto.from(news);
  }

  @Transactional(readOnly = true)
  public PageResponse<NewsSummaryDto> searchNewsList(NewsSearchCriteria request,
      CurrentUserInfoDto userInfoDto) {
    boolean isMaster = userInfoDto.role() == UserRole.ROLE_MASTER;
    Pagination<News> pagination = newsRepository.searchNewsList(request, isMaster);
    return PageResponse.from(pagination).map(NewsSummaryDto::from);
  }
}
