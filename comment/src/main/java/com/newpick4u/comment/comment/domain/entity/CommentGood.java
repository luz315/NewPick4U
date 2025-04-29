package com.newpick4u.comment.comment.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "p_comment_good")
@Entity
public class CommentGood {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "comment_good_id")
  private Long id;

  @JoinColumn(name = "comment_id")
  @ManyToOne(fetch = FetchType.LAZY)
  private Comment comment;

  private Long userId;

  private CommentGood(Comment comment, Long userId) {
    this.comment = comment;
    this.userId = userId;
  }

  public static CommentGood create(Comment comment, Long userId) {
    return new CommentGood(comment, userId);
  }
}
