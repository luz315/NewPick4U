package com.newpick4u.thread.thread.infrastructure.jpa;

import com.newpick4u.thread.thread.domain.entity.Thread;
import com.newpick4u.thread.thread.domain.entity.ThreadStatus;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ThreadJpaRepository extends JpaRepository<Thread, UUID> {

  Optional<Thread> findByTagName(String tagName);

  List<Thread> findAllByStatus(ThreadStatus threadStatus);

  @Modifying(flushAutomatically = true)
  @Query("UPDATE Thread t "
      + "SET t.score = t.score + 1 "
      + "WHERE t.tagName IN :existingTags")
  void incrementScoreForTags(Set<String> existingTags);

  Optional<Thread> findTop1ByStatusOrderByScoreAsc(ThreadStatus threadStatus);
}
