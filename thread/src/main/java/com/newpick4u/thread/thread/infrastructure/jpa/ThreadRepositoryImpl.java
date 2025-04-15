package com.newpick4u.thread.thread.infrastructure.jpa;

import com.newpick4u.thread.thread.domain.entity.Thread;
import com.newpick4u.thread.thread.domain.entity.ThreadStatus;
import com.newpick4u.thread.thread.domain.repository.ThreadRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ThreadRepositoryImpl implements ThreadRepository {

  private final ThreadJpaRepository threadJpaRepository;
  private final ThreadRepositoryCustom threadRepositoryCustom;

  @Override
  public Page<Thread> findAll(Pageable pageable) {
    return threadJpaRepository.findAll(pageable);
  }

  @Override
  public Optional<Thread> findById(UUID threadId) {
    return threadJpaRepository.findById(threadId);
  }

  @Override
  public Optional<Thread> findByTagName(String tagName) {
    return threadJpaRepository.findByTagName(tagName);
  }

  @Override
  public Thread save(Thread thread) {
    return threadJpaRepository.save(thread);
  }

  @Override
  public List<Thread> findAllByStatus(ThreadStatus threadStatus) {
    return threadJpaRepository.findAllByStatus(threadStatus);
  }

  @Override
  public void deleteAll() {
    threadJpaRepository.deleteAll();
  }
}
