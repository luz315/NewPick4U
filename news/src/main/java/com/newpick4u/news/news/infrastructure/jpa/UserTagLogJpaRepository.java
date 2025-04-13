package com.newpick4u.news.news.infrastructure.jpa;


import com.newpick4u.news.news.domain.entity.UserTagLog;
import com.newpick4u.news.news.domain.repository.UserTagLogRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserTagLogJpaRepository extends JpaRepository<UserTagLog, UUID>, UserTagLogRepository {
    Optional<UserTagLog> findByUserId(Long userId);
}
