package com.newpick4u.comment.global.common;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CommonUtil {

  private static final DateTimeFormatter FORMATTER = DateTimeFormatter
      .ofPattern("yyyy-MM-dd HH:mm:ss");

  /**
   * LocalDateTime → "YYYY-MM-DD HH:mm:SS" 문자열로 변환
   */
  public static String parseLDTToString(LocalDateTime dateTime) {
    if (dateTime == null) {
      return "";
    }
    return dateTime.format(FORMATTER);
  }
}
