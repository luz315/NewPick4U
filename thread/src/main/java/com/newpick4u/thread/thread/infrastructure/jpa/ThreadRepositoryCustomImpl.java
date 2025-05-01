package com.newpick4u.thread.thread.infrastructure.jpa;

import static com.newpick4u.thread.thread.domain.entity.QThread.thread;

import com.newpick4u.thread.thread.domain.entity.Thread;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class ThreadRepositoryCustomImpl implements ThreadRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  @Override
  public Slice<Thread> findSliceBy(Pageable pageable) {
    List<Thread> result = queryFactory
        .selectFrom(thread)
        .orderBy(thread.createdAt.desc())
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize() + 1) // 다음 페이지 여부 판단
        .fetch();

    boolean hasNext = result.size() > pageable.getPageSize();

    if (hasNext) {
      result.remove(result.size() - 1);
    }

    return new SliceImpl<>(result, pageable, hasNext);
  }
}
