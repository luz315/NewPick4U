package com.newpick4u.comment.comment.domain.entity;

import com.newpick4u.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "p_comment")
@Entity
public class Comment extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "comment_id")
  private UUID id;

  private UUID newsId;

  private UUID threadId;

  @Column(columnDefinition = "TEXT")
  private String content;

  private Long goodCount = 0L;

  @OneToMany(mappedBy = "comment")
  private List<CommentGood> goodList = new ArrayList<>();

  @Builder
  private Comment(UUID newsId, UUID threadId, String content) {
    this.newsId = newsId;
    this.threadId = threadId;
    this.content = content;
    this.goodCount = 0L;
    this.goodList = new ArrayList<>();
  }

  public static Comment createForNews(UUID newsId, String content) {
    return Comment.builder()
        .newsId(newsId)
        .threadId(null)
        .content(content)
        .build();
  }

  public static Comment createForThread(UUID threadId, String content) {
    return Comment.builder()
        .newsId(null)
        .threadId(threadId)
        .content(content)
        .build();
  }

  public void updateContent(String content) {
    this.content = content;
  }
}
