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
import com.newpick4u.news.news.domain.entity.UserTagLog;
import com.newpick4u.news.news.domain.model.Pagination;
import com.newpick4u.news.news.domain.repository.NewsRepository;
import com.newpick4u.news.news.domain.repository.TagInboxRepository;
import com.newpick4u.news.news.domain.repository.UserTagLogRepository;
import com.newpick4u.news.news.infrastructure.util.NewsRecommender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NewsServiceImpl implements NewsService {
    private final NewsRepository newsRepository;
    private final UserTagLogRepository userTagLogRepository;
    private final TagInboxRepository tagInboxRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
      public void saveNewsInfo(NewsInfoDto dto) {
          if (newsRepository.existsByAiNewsId(dto.aiNewsId())) {
              throw new IllegalStateException("이미 저장된 뉴스입니다: " + dto.aiNewsId());
          }
          News news = News.create(dto.aiNewsId(), dto.title(), dto.content(), dto.url(), dto.publishedDate(), 0L);
          newsRepository.save(news);
      }

    @Override
    @Transactional
    public void updateNewsTagList(NewsTagDto dto) {
        validateTagListSize(dto);
        newsRepository.findByAiNewsId(dto.aiNewsId())
                .ifPresentOrElse(
                        news -> applyTagList(news, dto),
                        () -> saveInbox(dto)
                );
    }

    // 내부 메서드
    private void validateTagListSize(NewsTagDto dto) {
        if (dto.tagList() == null || dto.tagList().size() > 11) {
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

    @Override
    @Transactional(readOnly = true)
    public NewsResponseDto getNews(UUID id, CurrentUserInfoDto userInfoDto) {
        boolean isMaster = userInfoDto.role() == UserRole.ROLE_MASTER;
        News news = newsRepository.findNewsByRole(id, isMaster)
                .orElseThrow(() -> new IllegalArgumentException("뉴스를 찾을 수 없습니다."));
        return NewsResponseDto.from(news);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<NewsSummaryDto> searchNewsList(NewsSearchCriteria request, CurrentUserInfoDto userInfoDto) {
        boolean isMaster = userInfoDto.role() == UserRole.ROLE_MASTER;
        Pagination<News> pagination = newsRepository.searchNewsList(request, isMaster);
        return PageResponse.from(pagination).map(NewsSummaryDto::from);
    }

    /**
     * 사용자 태그 로그를 기반으로 KNN + Content-Based 추천 뉴스 10개 반환
     */
    @Override
    @Transactional(readOnly = true)
    public List<NewsSummaryDto> recommendTop10(CurrentUserInfoDto userInfo) {
        Long userId = userInfo.userId();
        UserTagLog userLog = userTagLogRepository.findByUserId(userId)
                .orElse(null);
        if (userLog==null) return List.of();

        List<UserTagLog> allLogs = userTagLogRepository.findAll();
        List<News> allNews = newsRepository.findAllActive();

        // Content-Based 추천
        List<News> contentBased = NewsRecommender.recommendContentBased(userLog, allNews);

        // KNN 기반 추천
        List<News> knnBased = NewsRecommender.recommendKnnBased(userId, userLog, allLogs, allNews);

        // 통합 (중복 제거)
        Set<News> combined = new LinkedHashSet<>();
        combined.addAll(contentBased);
        combined.addAll(knnBased);

        return combined.stream()
                .limit(10)
                .map(NewsSummaryDto::from)
                .toList();
    }

    @Override
    @Transactional
    public void logUserTags(UUID newsId, Long userId) {
        News news = newsRepository.findNewsByRole(newsId, false)
                .orElseThrow(() -> new IllegalArgumentException()); // 예외 처리

        List<String> tags = news.getNewsTagList().stream()
                .map(tag -> tag.getName())
                .toList();

        UserTagLog userTagLog = userTagLogRepository.findByUserId(userId)
                .orElse(UserTagLog.create(userId));

        userTagLog.addTags(tags);
        userTagLogRepository.save(userTagLog);
    }
}
