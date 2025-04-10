package com.newpick4u.ainews.ainews.infrastructure.ai.dto;

import com.newpick4u.ainews.ainews.infrastructure.ai.dto.GeminiRequestDto.PartDto.TextDto;
import java.util.ArrayList;
import java.util.List;

public record GeminiRequestDto(
    List<PartDto> contents
) {

  public static GeminiRequestDto of(String text) {
    TextDto textDto = new TextDto(text);
    ArrayList<TextDto> parts = new ArrayList<>();
    parts.add(textDto);
    PartDto partDto = new PartDto(parts);
    ArrayList<PartDto> contents = new ArrayList<>();
    contents.add(partDto);

    return new GeminiRequestDto(contents);
  }

  public record PartDto(
      List<TextDto> parts
  ) {

    public record TextDto(
        String text
    ) {

    }
  }
}
