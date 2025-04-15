package com.newpick4u.thread.thread.infrastructure.jpa;

import com.newpick4u.thread.thread.domain.entity.Thread;
import com.newpick4u.thread.thread.domain.entity.ThreadStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ThreadJpaRepository extends JpaRepository<Thread, UUID> {

  int countByStatus(ThreadStatus threadStatus);

  Optional<Thread> findByTagName(String tagName);

  List<Thread> findAllByStatus(ThreadStatus threadStatus);
}
