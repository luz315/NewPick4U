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
public class NewsRepositoryimpl implements NewsRepository {

    private final NewsJpaRepository newsJpaRepository;
    private final NewsRepositoryCustom newsRepositoryCustom;

    @Override
    public News save(News news) {
        return newsJpaRepository.save(news);
    }

    @Override
    public Optional<News> findByAiNewsId(String aiNewsId) {
        return newsJpaRepository.findByAiNewsId(aiNewsId);
    }

    @Override
    public boolean existsByAiNewsId(String aiNewsId) {
        return newsJpaRepository.existsByAiNewsId(aiNewsId);
    }

    @Override
    public Pagination<News> searchNewsList(NewsSearchCriteria criteria, boolean isMaster) {
        return newsRepositoryCustom.searchNewsList(criteria, isMaster);
    }

    @Override
    public Optional<News> findNewsByRole(UUID id, boolean isMaster) {
        return newsRepositoryCustom.findNewsByRole(id, isMaster);
    }


}
