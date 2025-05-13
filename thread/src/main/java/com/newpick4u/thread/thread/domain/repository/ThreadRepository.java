package com.newpick4u.thread.thread.domain.repository;

import com.newpick4u.thread.thread.domain.entity.Thread;
import com.newpick4u.thread.thread.domain.entity.ThreadStatus;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface ThreadRepository {

  Slice<Thread> findAll(Pageable pageable);

  Optional<Thread> findById(UUID threadId);

  Optional<Thread> findByTagName(String tagName);

  Thread save(Thread thread);

  List<Thread> findAllByStatus(ThreadStatus threadStatus);

  void deleteAll();

  void incrementScoreForTags(Set<String> existingTags);

  void saveAll(List<Thread> toCreate);

  Optional<Thread> findTop1ByStatusOrderByScoreAsc(ThreadStatus threadStatus);
}
