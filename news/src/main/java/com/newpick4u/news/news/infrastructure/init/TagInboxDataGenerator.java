package com.newpick4u.news.news.infrastructure.init;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.newpick4u.news.news.application.dto.NewsTagDto;
import com.newpick4u.news.news.domain.entity.TagInbox;
import com.newpick4u.news.news.domain.repository.TagInboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TagInboxDataGenerator {

    private final TagInboxRepository tagInboxRepository;
    private final ObjectMapper objectMapper;

    @Bean(name = "tagInboxDataGeneratorInit")
    public CommandLineRunner init() {
        return args -> {
            createTagInboxData();
        };
    }

    private void createTagInboxData() {
        // aiNewsId 고정 값으로 설정
        String aiNewsId = "news-12345";  // aiNewsId를 "news-12345"로 설정

        // 예시 태그 이름 목록 생성
        List<String> tagNames = List.of("정치", "경제", "기술", "문화", "스포츠");

        // List<String> -> List<TagDto>로 변환
        List<NewsTagDto.TagDto> tagDtos = tagNames.stream()
                .map(tagName -> new NewsTagDto.TagDto(UUID.randomUUID(), tagName)) // UUID를 생성하여 TagDto로 변환
                .collect(Collectors.toList());

        // NewsTagDto 생성
        NewsTagDto dto = new NewsTagDto(aiNewsId, tagDtos);

        try {
            // dto를 JSON으로 직렬화
            String jsonPayload = objectMapper.writeValueAsString(dto);

            // TagInbox 생성 및 저장
            TagInbox inbox = TagInbox.create(aiNewsId, jsonPayload);
            tagInboxRepository.save(inbox);

            System.out.println("TagInbox 데이터 생성 완료: aiNewsId=" + aiNewsId);

        } catch (Exception e) {
            System.out.println("TagInbox 생성 중 오류 발생: " + e.getMessage());
        }
    }
}
