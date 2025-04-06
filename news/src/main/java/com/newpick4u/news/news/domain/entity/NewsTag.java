package com.newpick4u.news.news.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "p_news_tag")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class NewsTag {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "news_tag_id")
    private UUID id;

    @Column(nullable = false)
    private UUID tagId;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "news_id", nullable = false)
    private News news;

    @Builder(access = AccessLevel.PRIVATE)
    private NewsTag(UUID tagId, String name, News news) {
        this.tagId = tagId;
        this.name = name;
        this.news = news;
    }

    public static NewsTag create(UUID tagId, String name, News news) {
        return NewsTag.builder()
                .tagId(tagId)
                .name(name)
                .news(news)
                .build();
    }
}
