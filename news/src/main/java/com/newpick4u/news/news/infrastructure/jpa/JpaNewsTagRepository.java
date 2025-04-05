package com.newpick4u.news.news.infrastructure.jpa;

import com.newpick4u.news.news.domain.entity.NewsTag;
import com.newpick4u.news.news.domain.repository.NewsTagRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface JpaNewsTagRepository extends JpaRepository<NewsTag, UUID>, NewsTagRepository {
}
