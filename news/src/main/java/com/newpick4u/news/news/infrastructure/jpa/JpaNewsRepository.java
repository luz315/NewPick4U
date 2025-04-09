package com.newpick4u.news.news.infrastructure.jpa;

import com.newpick4u.news.news.domain.entity.News;
import com.newpick4u.news.news.domain.repository.NewsRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaNewsRepository extends JpaRepository<News, UUID> {
    @Query("select n from News n join fetch n.newsTagList where n.id = :id")
    Optional<News> findDetail(@Param("id") UUID id);

    @Query("select n from News n join fetch n.newsTagList where n.id = :id and n.status = 'ACTIVE'")
    Optional<News> findActiveDetail(@Param("id") UUID id);

    Optional<News> findByAiNewsId(String aiNewsId);

    boolean existsByAiNewsId(String aiNewsId);
}
