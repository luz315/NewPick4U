package com.newpick4u.tag.infrastructure.jpa;

import com.newpick4u.tag.domain.entity.Tag;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface TagJpaRepository extends JpaRepository<Tag, UUID> {

  Optional<Tag> findByTagName(String tagName);

  List<Tag> findAllByTagNameIn(List<String> tagNames);

  @Modifying(flushAutomatically = true)
  @Query("""
          UPDATE Tag t
          SET t.score = t.score + 1
          WHERE t.tagName IN :tagNames
      """)
  void incrementScoreByTagNames(ArrayList<String> tagNames);
}
