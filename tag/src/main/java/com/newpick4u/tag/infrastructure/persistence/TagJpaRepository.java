package com.newpick4u.tag.infrastructure.persistence;

import com.newpick4u.tag.domain.entity.Tag;
import com.newpick4u.tag.domain.repository.TagRepository;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagJpaRepository extends JpaRepository<Tag, UUID>, TagJpaRepositoryCustom,
    TagRepository {

}
