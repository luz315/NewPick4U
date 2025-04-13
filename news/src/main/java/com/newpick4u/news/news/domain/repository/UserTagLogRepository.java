package com.newpick4u.news.news.domain.repository;

import com.newpick4u.news.news.domain.entity.UserTagLog;

import java.util.List;
import java.util.Optional;

public interface UserTagLogRepository {
    Optional<UserTagLog> findByUserId(Long userId);
    List<UserTagLog> findAll(); // 추천 벡터 전체 목록 계산용
    UserTagLog save(UserTagLog userTagLog); // 유저 로그 저장/업데이트
}
