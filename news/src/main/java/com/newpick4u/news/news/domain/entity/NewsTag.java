package com.newpick4u.news.news.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "p_news_tag")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class NewsTag {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "news_tag_id", nullable = false, unique = true)
    private UUID id;

    @Column(nullable = false)
    private UUID tagId;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "news_id", nullable = false)
    private News news;

    public static NewsTag create(UUID tagId, String name, News news) {
        return new NewsTag(null,tagId,name,news);
    }
}
