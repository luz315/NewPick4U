package com.newpick4u.news.news.infrastructure.jpa;

import com.newpick4u.news.news.domain.critria.NewsSearchCriteria;
import com.newpick4u.news.news.domain.entity.News;
import com.newpick4u.news.news.domain.model.Pagination;
import com.newpick4u.news.news.domain.repository.NewsRepository;
import com.newpick4u.news.news.domain.repository.NewsRepositoryCustom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class NewsRepositoryAdapter implements NewsRepository {

    private final JpaNewsRepository jpaNewsRepository;
    private final NewsRepositoryCustom newsRepositoryCustom;

    @Override
    public News save(News news) {
        return jpaNewsRepository.save(news);
    }

    @Override
    public Optional<News> findByAiNewsId(String aiNewsId) {
        return jpaNewsRepository.findByAiNewsId(aiNewsId);
    }

    @Override
    public boolean existsByAiNewsId(String aiNewsId) {
        return jpaNewsRepository.existsByAiNewsId(aiNewsId);
    }

    @Override
    public Optional<News> findById(UUID id) {
        return jpaNewsRepository.findById(id);
    }

    @Override
    public Pagination<News> searchNewsList(NewsSearchCriteria criteria) {
        return newsRepositoryCustom.searchNewsList(criteria);
    }

    @Override
    public Optional<News> findDetail(UUID id) {
        return jpaNewsRepository.findDetail(id);
    }


}
