package com.newpick4u.ainews.ainews.infrastructure.ai;

import com.newpick4u.ainews.ainews.infrastructure.ai.dto.GeminiRequestDto;
import com.newpick4u.ainews.global.config.FeignClientConfig;
import com.newpick4u.ainews.global.config.FeignGeminiClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "gemini-service", url = "${app.client.gemini.host}",
    configuration = {FeignClientConfig.class, FeignGeminiClientConfig.class})
public interface GeminiFeignClient {

  @PostMapping("${app.client.gemini.url}")
  String processGemini(@RequestParam("key") String key,
      @RequestBody GeminiRequestDto params);
}
