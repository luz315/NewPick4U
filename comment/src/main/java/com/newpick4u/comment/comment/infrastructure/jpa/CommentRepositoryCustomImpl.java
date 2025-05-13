package com.newpick4u.comment.comment.infrastructure.jpa;

import static com.newpick4u.comment.comment.domain.entity.QComment.comment;
import static com.newpick4u.comment.comment.domain.entity.QCommentGood.commentGood;

import com.newpick4u.comment.comment.application.dto.CommentWithGoodDto;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class CommentRepositoryCustomImpl implements CommentRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  @Override
  public Page<CommentWithGoodDto> findCommentsWithUserGood(
      UUID newsId, UUID threadId, Long userId, Pageable pageable) {

    BooleanBuilder whereCondition = new BooleanBuilder();
    if (newsId != null) {
      whereCondition.and(comment.newsId.eq(newsId));
    } else if (threadId != null) {
      whereCondition.and(comment.threadId.eq(threadId));
    }

    // 정렬 기준
    OrderSpecifier<?>[] orderSpecifiers = QueryDslUtil.getAllOrderSpecifierArr(pageable, comment);

    List<CommentWithGoodDto> contents = queryFactory
        .select(
            Projections.constructor(
                CommentWithGoodDto.class,
                comment.id,
                comment.newsId,
                comment.threadId,
                comment.content,
                comment.goodCount,
                commentGood.id,  // 존재하지 않을 수 있음
                comment.createdAt,
                comment.updatedAt
            ))
        .from(comment)
        .leftJoin(comment.goodList, commentGood)
        .on(
            commentGood.userId.eq(userId)
                .and(commentGood.comment.eq(comment))
        )
        .where(
            whereCondition,
            comment.deletedAt.isNull()
        )
        .orderBy(orderSpecifiers)
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    JPAQuery<Long> countQuery = queryFactory
        .select(comment.count())
        .from(comment)
        .where(
            whereCondition,
            comment.deletedAt.isNull()
        );
    return PageableExecutionUtils.getPage(contents, pageable, countQuery::fetchOne);
  }
}
