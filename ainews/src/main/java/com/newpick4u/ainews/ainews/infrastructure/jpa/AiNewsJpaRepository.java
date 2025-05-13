package com.newpick4u.ainews.ainews.infrastructure.jpa;

import com.newpick4u.ainews.ainews.domain.entity.AiNews;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AiNewsJpaRepository extends JpaRepository<AiNews, UUID> {

  Optional<AiNews> findByOriginNewsId(UUID uuid);
}
