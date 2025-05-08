package com.newpick4u.client.client.application.dto.response;

import com.newpick4u.client.client.domain.entity.ClientDocument;
import java.util.UUID;
import lombok.Getter;

@Getter
public class SearchClientResponseDto {

  private UUID id;
  private String name;
  private String address;
  private String email;
  private String industry;

  private SearchClientResponseDto(UUID id, String name, String address, String email,
      String industry) {
    this.id = id;
    this.name = name;
    this.address = address;
    this.email = email;
    this.industry = industry;
  }

  public static SearchClientResponseDto from(ClientDocument doc) {
    return new SearchClientResponseDto(
        doc.getId(),
        doc.getName(),
        doc.getAddress(),
        doc.getEmail(),
        doc.getIndustry()
    );
  }
}
