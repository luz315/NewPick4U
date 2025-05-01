package com.newpick4u.news.news.infrastructure.jpa;

import com.newpick4u.news.news.domain.entity.News;
import com.newpick4u.news.news.domain.repository.projection.NewsCreatedInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface NewsJpaRepository extends JpaRepository<News, UUID> {
    Optional<News> findByAiNewsId(String aiNewsId);
    boolean existsByAiNewsId(String aiNewsId);
    List<News> findAll();
    @Modifying
    @Query("UPDATE News n SET n.view = n.view + :count WHERE n.id = :newsId")
    void incrementViewCount(@Param("newsId") UUID newsId, @Param("count") long count);
    @Query("SELECT n.id AS id, n.createdAt AS createdAt FROM News n WHERE n.status = com.newpick4u.news.news.domain.entity.NewsStatus.ACTIVE")
    List<NewsCreatedInfo> findAllActiveNewsCreatedInfos();
}
