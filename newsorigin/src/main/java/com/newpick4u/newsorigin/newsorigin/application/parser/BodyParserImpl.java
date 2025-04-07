package com.newpick4u.newsorigin.newsorigin.application.parser;

import io.micrometer.common.util.StringUtils;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BodyParserImpl implements BodyParser {

  private static final String[] EXTRACT_TARGET_ARR = {"article", "section", "div", "main"};
  private static final String REMOVE_TARGET_TAG_NAME = "script, style, nav, header, footer, aside, noscript, iframe";

  public String extractMainBody(String totalBody, String title) {
    String[] titleWords = title.split("[,\\s·]+|<b>|</b>");
    List<String> filtered = Arrays.stream(titleWords)
        .filter(s -> !s.isBlank())
        .filter(s -> s.length() > 1)
        .toList();

    Document doc = Jsoup.parse(totalBody);
    Element body = doc.body();

    String bestText = "";
    int bestMatch = 0;
    int maxLength = 0;

    // 본문 후보가 될 수 있는 블록 요소들
    for (String extractTarget : EXTRACT_TARGET_ARR) {
      Elements elements = body.select(extractTarget);
      for (Element element : elements) {
        // script/style 등은 제거
        element.select(REMOVE_TARGET_TAG_NAME).remove();

        String text = element.text().trim();

        int matchCount = 0;
        for (String titleWord : filtered) {
          if (StringUtils.isEmpty(titleWord)) {
            continue;
          }
          if (text.contains(titleWord)) {
            matchCount++;
          }
        }

        if (text.length() > maxLength && matchCount > bestMatch) {
          maxLength = text.length();
          bestMatch = matchCount;
          bestText = text;
        }

        if (matchCount > 3
            && text.length() > maxLength && matchCount < bestMatch) {
          maxLength = text.length();
          bestMatch = matchCount;
          bestText = text;
        }
      }
    }

    return bestText;
  }

}
