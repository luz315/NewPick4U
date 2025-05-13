package com.newpick4u.thread.thread.infrastructure.jpa;

import com.newpick4u.thread.thread.domain.entity.Thread;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface ThreadRepositoryCustom {

  Slice<Thread> findSliceBy(Pageable pageable);
}
