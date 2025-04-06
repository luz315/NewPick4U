package com.newpick4u.news.news.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "p_news")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class News {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "news_id")
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column
    private Long view;

    @OneToMany(mappedBy = "news", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 10)
    private List<NewsTag> newsTagList;

    @Builder(access = AccessLevel.PRIVATE)
    private News(String title, String content, Long view, List<NewsTag> newsTagList) {
        this.title = title;
        this.content = content;
        this.view = view;
        this.newsTagList = newsTagList != null ? newsTagList : new ArrayList<>();
    }

    public static News create(String title, String content, Long view) {
        return News.builder()
                .title(title)
                .content(content)
                .view(view)
                .build();
    }

    public void updateNewsTags(List<NewsTag> newTags) {
        this.newsTagList.addAll(newTags);
    }

}
