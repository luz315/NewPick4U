package com.newpick4u.thread.thread.domain.entity;

import static com.newpick4u.thread.thread.domain.entity.ThreadStatus.CLOSED;
import static com.newpick4u.thread.thread.domain.entity.ThreadStatus.OPEN;

import com.newpick4u.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

  @Column(name = "tag_name", unique = true, nullable = false)
  private String tagName;

  @Column(columnDefinition = "TEXT")
  private String summary;

  private Long score;

  @Enumerated(EnumType.STRING)
  private ThreadStatus status;

  public static Thread create(String tagName) {
    return new Thread(tagName);
  }

  private Thread(String tagName) {
    this.tagName = tagName;
    this.score = 1L;
    this.status = OPEN;
  }

  public void closedThread() {
    this.status = CLOSED;
  }

  public void plusScore() {
    this.score++;
  }

  public void addSummary(String summary) {
    this.summary = summary;
  }
}
