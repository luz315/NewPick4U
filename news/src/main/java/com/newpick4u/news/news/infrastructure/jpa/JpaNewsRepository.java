package com.newpick4u.news.news.infrastructure.jpa;

import com.newpick4u.news.news.domain.entity.News;
import com.newpick4u.news.news.domain.repository.NewsRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface JpaNewsRepository extends JpaRepository<News, UUID>, NewsRepository {
}
