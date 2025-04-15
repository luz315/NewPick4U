package com.newpick4u.tag.application.usecase;

import com.newpick4u.tag.application.dto.AiNewsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TagMessageHandler {

  private final TagService tagService;

  public void handle(AiNewsDto dto) {
    tagService.createTagFromAi(dto);
  }

  public void deleteTagFromAi(AiNewsDto dto) {
    tagService.deleteTagFromAi(dto);
  }
}
