package com.newpick4u.newsorigin.global.common;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@Slf4j
class EnvDecoderTest {

  @Test
  @DisplayName("인코딩 디코딩 테스트")
  void encodeTest() {
    String encoded = EnvDecoder.encodeBase64("가");
    // log.info("encoded = {}", encoded);
    String decoded = EnvDecoder.decodeBase64(encoded);
    // log.info("decoded = {}", decoded);
    Assertions.assertEquals("가", decoded);
  }
}