package com.newpick4u.news.news.infrastructure.jpa;

import com.newpick4u.news.news.domain.critria.NewsSearchCriteria;
import com.newpick4u.news.news.domain.entity.News;
import com.newpick4u.news.news.domain.entity.NewsStatus;
import com.newpick4u.news.news.domain.entity.QNews;
import com.newpick4u.news.news.domain.entity.QNewsTag;
import com.newpick4u.news.news.domain.model.Pagination;
import com.newpick4u.news.news.domain.repository.NewsRepositoryCustom;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class NewsRepositoryCustomImpl implements NewsRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    static QNews news;
    static QNewsTag newsTag;

    // 복수조회
    @Override
    public Pagination<News> searchNewsList(NewsSearchCriteria request, boolean isMaster) {
        BooleanBuilder where = buildWhereClause(request, news, newsTag);
        OrderSpecifier<?> order = buildOrderSpecifier(request, news);

        applyRoleFilter(where, isMaster);

        // 1차 쿼리: News ID만 조회 (페이징 적용)
        List<UUID> newsIds = queryFactory
                .select(news.id)
                .from(news)
                .leftJoin(news.newsTagList, newsTag)
                .where(where)
                .orderBy(order)
                .offset((long) (request.page() - 1) * request.size())
                .limit(request.size())
                .distinct()
                .fetch();

        if (newsIds.isEmpty()) {
            return Pagination.of(List.of(), request.page(), request.size(), 0L);
        }

        // 2차 쿼리: fetch join으로 실제 데이터 조회
        List<News> result = queryFactory
                .selectFrom(news)
                .leftJoin(news.newsTagList, newsTag).fetchJoin()
                .where(news.id.in(newsIds))
                .orderBy(order)
                .distinct()
                .fetch();

        // 전체 카운트 (페이징용)
        Long total = queryFactory
                .select(news.countDistinct())
                .from(news)
                .leftJoin(news.newsTagList, newsTag)
                .where(where)
                .fetchOne();

        return Pagination.of(result, request.page(), request.size(), total != null ? total : 0);
    }

    // 내부메서드
    private BooleanBuilder buildWhereClause(NewsSearchCriteria request, QNews news, QNewsTag newsTag) {
        BooleanBuilder where = new BooleanBuilder();
        if (request.keyword() != null && !request.keyword().isBlank()) {
            switch (request.filter()) {
                case "tag" -> where.and(newsTag.name.containsIgnoreCase(request.keyword()));
                case "title" -> where.and(news.title.containsIgnoreCase(request.keyword()));
                default -> where.and(news.title.containsIgnoreCase(request.keyword()));
            }
        }
        return where;
    }

    private OrderSpecifier<?> buildOrderSpecifier(NewsSearchCriteria request, QNews news) {
        if (request.sort() != null && !request.sort().isBlank()) {
            return switch (request.sort()) {
                case "comment" -> news.createdAt.desc();
                case "view" -> news.view.desc();
                case "latest" -> news.createdAt.desc();
                default -> news.createdAt.desc();
            };
        }
        return news.createdAt.desc();
    }

    // 단건조회
    @Override
    public Optional<News> findNewsByRole(UUID id, boolean isMaster) {
        BooleanBuilder where = new BooleanBuilder().and(news.id.eq(id));

        applyRoleFilter(where, isMaster);

        return Optional.ofNullable(
                queryFactory
                        .selectFrom(news)
                        .leftJoin(news.newsTagList, newsTag).fetchJoin()
                        .where(where)
                        .fetchOne()
        );
    }

    // 내부메서드
    private void applyRoleFilter(BooleanBuilder where, boolean isMaster) {
        if (!isMaster) {
            where.and(news.status.eq(NewsStatus.ACTIVE));
        }
    }

    // 테스트용 메서드
    @Override
    public Optional<News> findWithTagsByAiNewsId(String aiNewsId) {
        QNews news = QNews.news;
        QNewsTag newsTag = QNewsTag.newsTag;

        return Optional.ofNullable(
                queryFactory
                        .selectFrom(news)
                        .leftJoin(news.newsTagList, newsTag).fetchJoin()
                        .where(news.aiNewsId.eq(aiNewsId))
                        .fetchOne()
        );
    }
}
