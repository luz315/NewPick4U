package com.newpick4u.news.news.infrastructure.init;

import com.newpick4u.news.news.domain.entity.News;
import com.newpick4u.news.news.domain.entity.NewsTag;
import com.newpick4u.news.news.domain.repository.NewsRepository;
import com.newpick4u.news.news.domain.entity.NewsStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class NewsDataGenerator {

    private final NewsRepository newsRepository;

    @Bean
    public CommandLineRunner init() {
        return args -> {
            createNewsData();
        };
    }

    private void createNewsData() {
        // 뉴스 데이터를 20개 생성
        for (int i = 1; i <= 20; i++) {
            String aiNewsId = "news-" + UUID.randomUUID(); // UUID를 사용하여 고유한 ai_news_id 생성
            String title = "뉴스 제목 " + i;
            String content = "뉴스 내용 " + i;
            String url = "http://news.example.com/" + i;
            String publishedDate = "2025-04-0" + i; // 예시 날짜

            News news = News.create(aiNewsId, title, content, url, publishedDate, 0L);

            // 뉴스에 태그 추가
            createNewsTags(news, i);

            // 뉴스 저장 (NewsTag는 자동으로 Cascade 저장됨)
            newsRepository.save(news);
        }

        System.out.println("뉴스 데이터 생성 완료");
    }

    private void createNewsTags(News news, int newsId) {
        // 다양한 태그를 추가 (예시)
        List<String> tags = Arrays.asList(
                "정치", "경제", "사회", "문화", "기술", "스포츠", "음악", "영화", "여행", "음식",
                "날씨", "과학", "환경", "교육", "건강", "금융", "투자", "주식", "채권",
                "기후 변화", "지구 온난화", "모바일", "웹", "클라우드", "AI", "빅데이터",
                "축구", "야구", "농구", "배드민턴", "영화 리뷰", "드라마", "TV 쇼", "연예"
        );

        // 태그를 무작위로 섞기
        Collections.shuffle(tags);  // 태그 목록 무작위로 섞기

        // 태그 중 일부를 무작위로 선택하여 뉴스에 추가
        int tagCount = 5; // 예시로 5개의 태그를 선택
        List<String> selectedTags = tags.subList(0, tagCount); // 처음 5개 태그 선택

        selectedTags.forEach(tagName -> {
            // 각 뉴스에 태그 추가
            NewsTag newsTag = NewsTag.create(UUID.randomUUID(), tagName, news);
            news.addTags(Arrays.asList(newsTag)); // 태그 리스트에 추가
        });
    }
}
