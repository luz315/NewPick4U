package com.newpick4u.client.client.domain.entity;

import jakarta.persistence.Id;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "client")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientDocument {

  @Id
  private UUID id;

  @Field(type = FieldType.Text, analyzer = "ngram_analyzer", searchAnalyzer = "ngram_analyzer")
  private String name;

  @Field(type = FieldType.Text, analyzer = "ngram_analyzer", searchAnalyzer = "ngram_analyzer")
  private String address;

  @Field(type = FieldType.Text, analyzer = "ngram_analyzer", searchAnalyzer = "ngram_analyzer")
  private String email;

  @Field(type = FieldType.Keyword) // ✅ 정확 일치 검색용
  private String phone;

  @Field(type = FieldType.Keyword)
  private String industry;

  public static ClientDocument from(UUID id, String name, String address, String email,
      String phone, String industry) {
    return ClientDocument.builder()
        .id(id)
        .name(name)
        .address(address)
        .email(email)
        .phone(phone)
        .industry(industry)
        .build();
  }
}
