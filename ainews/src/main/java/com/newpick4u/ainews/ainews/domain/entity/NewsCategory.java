package com.newpick4u.ainews.ainews.domain.entity;

import lombok.Getter;

@Getter
public enum NewsCategory {
  IT_SCIENCE("IT_과학"),
  SPORTS("스포츠"),
  LOCAL("지역"),
  INTERNATIONAL("국제"),
  CULTURE("문화"),
  SOCIETY("사회"),
  ECONOMY("경제"),
  POLITICS("정치");

  private final String koreanName;

  NewsCategory(String koreanName) {
    this.koreanName = koreanName;
  }

  public static String getKoreanNames() {
    StringBuilder sb = new StringBuilder();
    NewsCategory[] values = NewsCategory.values();
    for (NewsCategory value : values) {
      sb.append(value.getKoreanName());
      sb.append(",");
    }
    sb.deleteCharAt(sb.length() - 1);
    return sb.toString();
  }

  public String getKoreanName() {
    return koreanName;
  }
}

