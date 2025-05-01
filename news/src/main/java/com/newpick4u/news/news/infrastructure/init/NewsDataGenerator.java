//package com.newpick4u.news.news.infrastructure.init;
//
//import com.newpick4u.news.news.domain.entity.News;
//import com.newpick4u.news.news.domain.entity.NewsTag;
//import com.newpick4u.news.news.domain.repository.NewsRepository;
//import jakarta.annotation.PostConstruct;
//import lombok.RequiredArgsConstructor;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Profile;
//import org.springframework.stereotype.Component;
//
//import java.io.BufferedWriter;
//import java.io.File;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.nio.file.Paths;
//import java.util.*;
//
//@Component
//@Profile("!test")
//@RequiredArgsConstructor
//public class NewsDataGenerator {
//
//    private final NewsRepository newsRepository;
//    private final Random random = new Random();
//
//    @PostConstruct
//    public void generateNewsData() {
//        List<String> tags = generateTagList();
//        List<UUID> newsIdList = new ArrayList<>();
//
//        for (int i = 0; i < 10000; i++) {
//            String aiNewsId = "news-" + UUID.randomUUID();
//            News news = News.create(
//                    aiNewsId,
//                    "뉴스 제목 " + (i + 1),
//                    "뉴스 본문 " + (i + 1),
//                    "http://news.example.com/" + (i + 1),
//                    "2025-04-" + ((i % 30) + 1),
//                    0L
//            );
//
//            // 태그 무작위로 5~9개 선택
//            Collections.shuffle(tags);
//            int tagCount = 5 + random.nextInt(5); // 5 ~ 9개
//            List<String> selectedTags = tags.subList(0, tagCount);
//
//            for (String tagName : selectedTags) {
//                NewsTag newsTag = NewsTag.create(UUID.randomUUID(), tagName, news);
//                news.addTags(Collections.singletonList(newsTag));
//            }
//
//            News savedNews = newsRepository.save(news);
//            newsIdList.add(savedNews.getId());
//        }
//
//        saveNewsIdsToFile(newsIdList);
//        System.out.println("[뉴스 10,000개 생성 완료]");
//    }
//
//    private void saveNewsIdsToFile(List<UUID> newsIds) {
//
//        System.out.println("###System.getProperty(\"app.root.path\") = " + System.getProperty("app.root.path"));
//        System.out.println("###System.getProperty(\"user.dir\") = " + System.getProperty("user.dir"));
//
////        String projectRoot = Paths.get("").toAbsolutePath().toString();
//        File file = new File(System.getProperty("user.dir"), "k6-news-ids.txt");
//
//        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
//            for (UUID newsId : newsIds) {
//                writer.write(newsId.toString());
//                writer.newLine();
//            }
//            System.out.println("[k6-news-ids.txt 파일 생성 완료]");
//        } catch (IOException e) {
//            System.err.println("뉴스 ID 저장 실패: " + e.getMessage());
//        }
//    }
//
//    private List<String> generateTagList() {
//        return Arrays.asList(
//                "정치", "경제", "사회", "문화", "기술", "스포츠", "음악", "영화", "여행", "음식",
//                "날씨", "과학", "환경", "교육", "건강", "금융", "투자", "주식", "채권",
//                "기후 변화", "지구 온난화", "모바일", "웹", "클라우드", "AI", "빅데이터",
//                "축구", "야구", "농구", "배드민턴", "영화 리뷰", "드라마", "TV 쇼", "연예",
//                "동물", "식물", "자동차", "항공우주", "철도", "패션", "뷰티", "게임", "VR", "AR",
//                "로봇공학", "우주 탐사", "블록체인", "NFT", "암호화폐", "헬스케어", "바이오테크",
//                "환경 보호", "신재생 에너지", "원자력", "해양학", "심리학", "철학", "역사", "문학",
//                "미술", "조각", "사진", "디자인", "인터넷", "앱 개발", "서버", "네트워크",
//                "사이버 보안", "클라우드 컴퓨팅", "빅데이터 분석", "머신러닝", "딥러닝", "자율주행",
//                "드론", "스마트 시티", "스마트 팩토리", "에듀테크", "핀테크", "인슈어테크",
//                "디지털 헬스", "스마트팜", "디지털 트윈", "메타버스", "디지털 전환",
//                "오픈소스", "UI/UX", "백엔드", "프론트엔드", "테스팅", "DevOps",
//                "프로젝트 관리", "스타트업", "창업", "VC 투자", "M&A", "컨설팅", "커리어",
//                "인재 채용", "HR 테크", "마케팅", "브랜딩", "PR", "SNS",
//                "광고", "콘텐츠", "유튜브", "틱톡", "인스타그램", "페이스북",
//                "카카오", "네이버", "구글", "아마존", "테슬라", "삼성", "애플",
//                "MS", "LG", "현대", "기아", "포스코", "SK", "롯데",
//                "부동산", "임대차", "주택", "상가", "토지", "건설", "인테리어",
//                "홈데코", "패션 트렌드", "K-POP", "K-드라마", "K-영화",
//                "해외여행", "국내여행", "캠핑", "등산", "피트니스", "헬스",
//                "다이어트", "영양", "요리", "디저트", "커피", "강남",
//                "와인", "칵테일", "맥주", "위스키", "술 문화",
//                "반려동물", "강아지", "고양이", "어류", "조류",
//                "농업", "수산업", "축산업", "에너지 산업", "IT 산업", "제조업",
//                "유통", "물류", "항공", "철도 교통", "택시", "배달 서비스",
//                "교육 정책", "입시", "학교", "대학교", "연구소",
//                "기후 정책", "환경 법규", "재난 관리", "국제 관계",
//                "외교", "군사", "전쟁", "평화", "UN", "NATO",
//                "스마트홈", "IoT", "웨어러블", "홈트레이닝", "라이프스타일"
//        );
//    }
//}
