package com.newpick4u.news.news.infrastructure.jpa;

import com.newpick4u.news.news.domain.entity.TagInbox;
import com.newpick4u.news.news.domain.repository.TagInboxRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class TagInboxRepositoryImpl implements TagInboxRepository {
    private final TagInboxJpaRepository tagInboxJpaRepository;

    @PersistenceContext
    private EntityManager em;

    @Override
    public TagInbox save(TagInbox tagInbox) {
        return tagInboxJpaRepository.save(tagInbox);
    }

    @Override
    public List<TagInbox> findAll() {
        return tagInboxJpaRepository.findAll();
    }

    @Override
    public void delete(TagInbox inbox) {
        tagInboxJpaRepository.deleteById(inbox.getId());
    }

    @Override
    public void flush() {
        tagInboxJpaRepository.flush();
    }
}
