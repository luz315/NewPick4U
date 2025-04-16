package com.newpick4u.tag.application.usecase;

import com.newpick4u.tag.application.dto.AiNewsDto;
import com.newpick4u.tag.application.dto.NewsTagDto.TagDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TagMessageHandler {

  private final TagService tagService;

  public List<TagDto> handle(AiNewsDto dto) {
    return tagService.createTagFromAi(dto);
  }

  public void deleteTagFromAi(AiNewsDto dto) {
    tagService.deleteTagFromAi(dto);
  }
}
