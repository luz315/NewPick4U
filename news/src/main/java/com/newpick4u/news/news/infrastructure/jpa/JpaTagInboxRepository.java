package com.newpick4u.news.news.infrastructure.jpa;

import com.newpick4u.news.news.domain.entity.TagInbox;
import com.newpick4u.news.news.domain.repository.TagInboxRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaTagInboxRepository  extends JpaRepository<TagInbox, UUID>, TagInboxRepository {
}
