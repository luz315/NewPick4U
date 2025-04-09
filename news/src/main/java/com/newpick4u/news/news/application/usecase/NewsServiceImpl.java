package com.newpick4u.news.news.application.usecase;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.newpick4u.news.news.application.dto.NewsInfoDto;
import com.newpick4u.news.news.application.dto.NewsTagDto;
import com.newpick4u.news.news.application.dto.response.NewsListResponse;
import com.newpick4u.news.news.application.dto.response.NewsResponseDto;
import com.newpick4u.news.news.domain.critria.NewsSearchCriteria;
import com.newpick4u.news.news.domain.entity.News;
import com.newpick4u.news.news.domain.entity.NewsTag;
import com.newpick4u.news.news.domain.entity.TagInbox;
import com.newpick4u.news.news.domain.model.Pagination;
import com.newpick4u.news.news.domain.repository.NewsRepository;
import com.newpick4u.news.news.domain.repository.TagInboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NewsServiceImpl implements NewsService {
    private final NewsRepository newsRepository;
    private final TagInboxRepository tagInboxRepository;
    private final ObjectMapper objectMapper;


    @Transactional
      public void saveNewsInfo(NewsInfoDto dto) {
          if (newsRepository.existsByAiNewsId(dto.aiNewsId())) {
              throw new IllegalStateException("이미 저장된 뉴스입니다: " + dto.aiNewsId());
          }

          News news = News.create(dto.aiNewsId(), dto.title(), dto.content(), 0L);
          newsRepository.save(news);
      }

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

    @Transactional(readOnly = true)
    public NewsResponseDto getNews(UUID id) {
        News news = newsRepository.findDetail(id)
                .orElseThrow(() -> new IllegalArgumentException("뉴스를 찾을 수 없습니다."));
        return NewsResponseDto.from(news);
    }

    @Transactional(readOnly = true)
    public NewsListResponse searchNewsList(NewsSearchCriteria request) {
        Pagination<News> pagination = newsRepository.searchNewsList(request);
        return NewsListResponse.from(pagination);
    }
}
