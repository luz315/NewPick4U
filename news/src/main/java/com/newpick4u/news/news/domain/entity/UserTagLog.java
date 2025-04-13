package com.newpick4u.news.news.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 사용자가 조회한 뉴스들의 태그 통계를 담는 객체
 */
@Entity
@Getter
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "p_user_tag_log")
public class UserTagLog {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_tag_log_id")
    private UUID id;

    @Column(nullable = false)
    private Long userId;

    @ElementCollection
    @CollectionTable(name = "user_tag_counts", joinColumns = @JoinColumn(name = "user_id"))
    @MapKeyColumn(name = "tag_name")  // Map의 key인 태그명을 어떤 컬럼으로 저장할지 명시
    @Column(name = "tag_count") // Map의 value인 count를 어떤 컬럼으로 저장할지 명시
    private Map<String, Integer> tagCount = new HashMap<>();

    @Builder(access = AccessLevel.PRIVATE)
    private UserTagLog(Long userId) {
        this.userId = userId;
    }

    public static UserTagLog create(Long userId) {
        return UserTagLog.builder()
                .userId(userId)
                .build();
    }

    public void addTag(String tag) {
        tagCount.put(tag, tagCount.getOrDefault(tag, 0) + 1);
    }

    public void addTags(Iterable<String> tags) {
        for (String tag : tags) {
            addTag(tag);
        }
    }
}
