package com.newpick4u.tag.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "p_tag")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Tag {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "tag_id", nullable = false, unique = true)
  private UUID tagId;

  @Column(nullable = false, unique = true)
  private String tagName;

  @ColumnDefault("1")
  private Long score;

  public static Tag create(String tagName, Long score) {
    return new Tag(null, tagName, score);
  }
}
