package com.newpick4u.ainews.ainews.infrastructure.ai.dto;


import com.newpick4u.ainews.ainews.infrastructure.ai.dto.GeminiResponseDto.Candidate.Content;
import com.newpick4u.ainews.ainews.infrastructure.ai.dto.GeminiResponseDto.Candidate.Content.Part;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public record GeminiResponseDto(
    List<Candidate> candidates,
    UsageMetadata usageMetadata,
    String modelVersion
) {

  public String getOnlyText() {
    try {
      Candidate candidate = candidates.get(0);
      Content content = candidate.content;
      Part part = content.parts.get(0);

      return part.text;

    } catch (Exception e) {
      log.error("parse error : ", e);
      return null;
    }
  }

  public record Candidate(
      Content content,
      String finishReason,
      double avgLogprobs
  ) {

    public record Content(
        List<Part> parts,
        String role
    ) {

      public record Part(
          String text
      ) {

      }
    }
  }

  public record UsageMetadata(
      int promptTokenCount,
      int candidatesTokenCount,
      int totalTokenCount,
      List<TokenDetail> promptTokensDetails,
      List<TokenDetail> candidatesTokensDetails
  ) {

    public record TokenDetail(
        String modality,
        int tokenCount
    ) {

    }
  }
}
