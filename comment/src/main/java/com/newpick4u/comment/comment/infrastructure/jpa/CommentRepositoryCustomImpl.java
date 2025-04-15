package com.newpick4u.comment.comment.infrastructure.jpa;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class CommentRepositoryCustomImpl implements CommentRepositoryCustom {

  private final JPAQueryFactory queryFactory;
}
