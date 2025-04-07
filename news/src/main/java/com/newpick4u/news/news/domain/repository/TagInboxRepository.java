package com.newpick4u.news.news.domain.repository;

import com.newpick4u.news.news.domain.entity.TagInbox;

import java.util.List;

public interface TagInboxRepository {
    TagInbox save(TagInbox tagInbox);
    List<TagInbox> findAll();
    void delete(TagInbox inbox);
}
