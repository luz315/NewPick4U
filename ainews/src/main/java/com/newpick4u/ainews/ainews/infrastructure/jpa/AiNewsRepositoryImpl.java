package com.newpick4u.ainews.ainews.infrastructure.jpa;

import com.newpick4u.ainews.ainews.domain.entity.AiNews;
import com.newpick4u.ainews.ainews.domain.repository.AiNewsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class AiNewsRepositoryImpl implements AiNewsRepository {

  private final AiNewsJpaRepository aiNewsJpaRepository;
  private final AiNewsRepositoryCustom aiNewsRepositoryCustom;

  public AiNews save(AiNews aiNews) {
    return aiNewsJpaRepository.save(aiNews);
  }
}
