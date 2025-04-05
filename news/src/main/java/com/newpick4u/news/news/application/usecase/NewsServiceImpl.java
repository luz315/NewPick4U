package com.newpick4u.news.news.application.usecase;

import com.newpick4u.news.news.domain.entity.News;
import com.newpick4u.news.news.domain.entity.NewsTag;
import com.newpick4u.news.news.domain.repository.NewsRepository;
import com.newpick4u.news.news.infrastructure.feign.TagClient;
import com.newpick4u.news.news.infrastructure.feign.dto.TagDto;
import com.newpick4u.news.news.infrastructure.kafka.dto.AiNewsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NewsServiceImpl implements NewsService {
    private final NewsRepository newsRepository;
    private final TagClient tagClient;

    @Transactional
    public void createNewsFromAi(AiNewsDto dto) {
        News news = News.create(dto.title(), dto.content(), 0L);

        List<TagDto> tags = tagClient.getOrCreateTags(dto.tags());

        List<NewsTag> newsTags = tags.stream()
                .map(tagDto -> NewsTag.create(
                        tagDto.id(),
                        tagDto.name(),
                        news)
                )
                .toList();

        news.updateNewsTags(newsTags); // cascade = ALL 로 News 저장 시 NewsTag도 저장됨

        newsRepository.save(news);
    }
}
