package com.newpick4u.ainews.ainews.application.dto;

import java.util.List;

public record ProceedAiNewsDto(
    String originalString,
    ProceedFields proceedFields
) {

  public static ProceedAiNewsDto of(String originalString, ProceedFields proceedFields) {
    return new ProceedAiNewsDto(originalString, proceedFields);
  }

  public record ProceedFields(
      String summary,
      List<String> tags
  ) {

    public String getTagsString() {
      StringBuilder sb = new StringBuilder();
      sb.append("[");
      for (String tag : tags) {
        sb.append(tag);
        sb.append(",");
      }
      sb.deleteCharAt(sb.length() - 1);
      sb.append("]");
      return sb.toString();
    }
  }
}
