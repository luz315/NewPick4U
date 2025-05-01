package com.newpick4u.news.news.domain.entity;

import com.newpick4u.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.time.LocalDateTime;
import java.util.ArrayList;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

@Entity
@Table(name = "p_news")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class News extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "news_id")
  private UUID id;

  @Column(nullable = false, unique = true)
  private String aiNewsId;

  @Column(nullable = false)
  private String title;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String content;

    @Column(nullable = false,  columnDefinition = "TEXT")
    private String url;

  @Column(nullable = false)
  private String publishedDate;

  @Enumerated(EnumType.STRING)
  @Column
  private NewsStatus status;

  @Column
  private Long view;

    @OneToMany(mappedBy = "news", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 10)
    private List<NewsTag> newsTagList = new ArrayList<>(); ;

  @Builder(access = AccessLevel.PRIVATE)
  private News(String aiNewsId, String title, String content, String url, String publishedDate,
      Long view, NewsStatus status) {
    this.aiNewsId = aiNewsId;
    this.title = title;
    this.content = content;
    this.url = url;
    this.publishedDate = publishedDate;
    this.view = view;
    this.status = status;
  }

  public static News create(String aiNewsId, String title, String content, String url,
      String publishedDate, Long view) {
    return News.builder()
        .aiNewsId(aiNewsId)
        .title(title)
        .content(content)
        .url(url)
        .publishedDate(publishedDate)
        .view(view)
        .status(NewsStatus.PENDING)
        .build();
  }

  public void addTags(List<NewsTag> newTags) {
    this.newsTagList.addAll(newTags);
    this.status = NewsStatus.ACTIVE;
  }

    public void updateView(Long view) {
        this.view = view;
    }

  public void markAsDeleted(LocalDateTime deleteAt, long deletedBy) {
    this.status = NewsStatus.DELETED;
    delete(deleteAt, deletedBy);
  }
}
