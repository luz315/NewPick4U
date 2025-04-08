package com.newpick4u.news.news.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "p_tag_inbox")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TagInbox {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String aiNewsId;

    @Lob
    @Column(nullable = false)
    private String jsonPayload; // 태그 데이터 전체를 JSON 형태로 보관 (ex. Jackson 사용)

    @Builder(access = AccessLevel.PRIVATE)
    private TagInbox(String aiNewsId, String jsonPayload) {
        this.aiNewsId = aiNewsId;
        this.jsonPayload = jsonPayload;
    }

    public static TagInbox create(String aiNewsId, String jsonPayload) {
        return TagInbox.builder()
                .aiNewsId(aiNewsId)
                .jsonPayload(jsonPayload)
                .build();
    }
}
