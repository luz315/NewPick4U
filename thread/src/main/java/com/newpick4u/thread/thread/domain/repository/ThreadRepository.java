package com.newpick4u.thread.thread.domain.repository;

import com.newpick4u.thread.thread.domain.entity.Thread;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ThreadRepository {

  Page<Thread> findAll(Pageable pageable);

  Optional<Thread> findById(UUID threadId);
}
