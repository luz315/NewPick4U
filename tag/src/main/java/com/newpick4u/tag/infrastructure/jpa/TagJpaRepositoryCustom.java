package com.newpick4u.tag.infrastructure.jpa;

import com.newpick4u.tag.domain.criteria.SearchTagCriteria;
import com.newpick4u.tag.domain.entity.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TagJpaRepositoryCustom {

  Page<Tag> searchByCriteria(SearchTagCriteria criteria, Pageable pageable);
}
