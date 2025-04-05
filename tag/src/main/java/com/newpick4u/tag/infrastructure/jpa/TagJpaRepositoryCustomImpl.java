package com.newpick4u.tag.infrastructure.jpa;

import static com.newpick4u.tag.domain.entity.QTag.tag;

import com.newpick4u.tag.domain.criteria.SearchTagCriteria;
import com.newpick4u.tag.domain.entity.Tag;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;


@RequiredArgsConstructor
public class TagJpaRepositoryCustomImpl implements TagJpaRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  @Override
  public Page<Tag> searchByCriteria(SearchTagCriteria criteria, Pageable pageable) {
    BooleanBuilder builder = new BooleanBuilder()
        .and(criteria.likeTagName())
        .and(criteria.betweenScore());

    List<Tag> content = queryFactory
        .selectFrom(tag)
        .where(builder)
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    Long total = queryFactory
        .select(tag.count())
        .from(tag)
        .where(builder)
        .fetchOne();

    return new PageImpl<>(content, pageable, total != null ? total : 0L);
  }
}
