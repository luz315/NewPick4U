package com.newpick4u.thread.thread.infrastructure.jpa;

import com.newpick4u.thread.thread.domain.entity.Thread;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ThreadJpaRepository extends JpaRepository<Thread, UUID> {

}
