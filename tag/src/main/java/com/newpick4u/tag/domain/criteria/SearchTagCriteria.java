package com.newpick4u.tag.domain.criteria;

import static com.newpick4u.tag.domain.entity.QTag.tag;

import com.querydsl.core.BooleanBuilder;
import lombok.Builder;

@Builder
public record SearchTagCriteria(
    String tagName,
    Long minScore,
    Long maxScore
) {

  public BooleanBuilder likeTagName() {
    if (tagName == null || tagName.isBlank()) {
      return new BooleanBuilder();
    }
    return new BooleanBuilder(tag.tagName.containsIgnoreCase(tagName));
  }

  public BooleanBuilder betweenScore() {
    if (minScore == null && maxScore == null) {
      return new BooleanBuilder();
    } else if (minScore != null && maxScore != null) {
      return new BooleanBuilder(tag.score.between(minScore, maxScore));
    } else if (minScore != null) {
      return new BooleanBuilder(tag.score.goe(minScore));
    } else {
      return new BooleanBuilder(tag.score.loe(maxScore));
    }
  }
}
