package com.newpick4u.news.news.presentation;

import com.newpick4u.news.news.application.scheduler.RecommendScheduler;
import com.newpick4u.news.news.application.scheduler.NewsVectorScheduler;
import com.newpick4u.news.news.application.scheduler.TagIndexSyncScheduler;
import com.newpick4u.news.news.application.scheduler.ViewCountScheduler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/news/admin/scheduler")
@RequiredArgsConstructor
@Slf4j
public class SchedulerAdminController {

    private final NewsVectorScheduler newsVectorScheduler;
    private final TagIndexSyncScheduler tagIndexSyncScheduler;
    private final RecommendScheduler recommendScheduler;
    private final ViewCountScheduler viewCountScheduler;

    @PostMapping("/sync-tags")
    public String syncPendingTags() {
        tagIndexSyncScheduler.syncPendingTagsToGlobalIndex();
        return "태그 인덱스 동기화 완료";
    }

    @PostMapping("/generate-vectors")
    public String generatePendingNewsVectors() {
        newsVectorScheduler.processPendingNewsVectors();
        return "뉴스 벡터 생성 완료";
    }

    @PostMapping("/update-recommendations")
    public String updateRecommendations() {
        recommendScheduler.updateAllUserRecommendations();
        return "사용자 추천 업데이트 완료";
    }

    @PostMapping("/sync-views")
    public String syncViewCounts() {
        viewCountScheduler.syncViewCounts();
        return "조회수 동기화 완료";
    }

    @PostMapping("/update-popularity")
    public String updatePopularityScores() {
        viewCountScheduler.updatePopularityScores();
        return "인기 점수 갱신 완료";
    }
}
