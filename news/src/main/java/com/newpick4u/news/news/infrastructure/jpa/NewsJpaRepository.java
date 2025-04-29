package com.newpick4u.news.news.infrastructure.jpa;

import com.newpick4u.news.news.domain.entity.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface NewsJpaRepository extends JpaRepository<News, UUID> {
    Optional<News> findByAiNewsId(String aiNewsId);
    boolean existsByAiNewsId(String aiNewsId);
    List<News> findAll();
}
