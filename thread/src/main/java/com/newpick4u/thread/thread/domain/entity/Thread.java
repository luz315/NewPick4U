package com.newpick4u.thread.thread.domain.entity;

import com.newpick4u.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "p_thread")
public class Thread extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(name = "news_id")
  private UUID newsId;

  private String summary;

  public static Thread create(UUID newsId, String summary) {
    return new Thread(newsId, summary);
  }

  private Thread(UUID newsId, String summary) {
    this.newsId = newsId;
    this.summary = summary;
  }
}
