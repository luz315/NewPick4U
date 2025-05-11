package com.newpick4u.newsorigin.global.common;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EnvDecoder {

  public static String encodeBase64(String original) {
    return Base64.getEncoder().encodeToString(original.getBytes(StandardCharsets.UTF_8));
  }

  public static String decodeBase64(String encoded) {
    return new String(Base64.getDecoder().decode(encoded), StandardCharsets.UTF_8);
  }
}