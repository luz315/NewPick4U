package com.newpick4u.news.news.application.usecase;

public interface TagIndexQueueOperator {
    void enqueuePendingTag(String tag);
    void flushPendingTagsToGlobalIndex(); // 스케줄러가 호출
}
