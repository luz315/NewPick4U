package com.newpick4u.news.news.domain.repository;

import com.newpick4u.news.news.domain.entity.TagInbox;

import java.util.List;
import java.util.Optional;

public interface TagInboxRepository {
    TagInbox save(TagInbox tagInbox);
    List<TagInbox> findAll();
    void delete(TagInbox inbox);
    void flush();
    Optional<TagInbox> findByAiNewsId(String aiNewsId);
}
