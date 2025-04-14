package com.newpick4u.ainews.ainews.infrastructure.jpa;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class AiNewsRepositoryCustomImpl implements AiNewsRepositoryCustom {

  private final JPAQueryFactory queryFactory;

}
