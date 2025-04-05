package com.newpick4u.tag.application.usecase;

import com.newpick4u.tag.application.dto.UpdateTagRequestDto;
import com.newpick4u.tag.domain.criteria.SearchTagCriteria;
import com.newpick4u.tag.domain.entity.Tag;
import com.newpick4u.tag.domain.repository.TagRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

  private final TagRepository tagRepository;

  @Override
  public Page<Tag> getTags(SearchTagCriteria criteria, Pageable pageable) {
    return tagRepository.searchByCriteria(criteria, pageable);
  }

  @Override
  @Transactional
  public void updateTag(UpdateTagRequestDto tag, UUID tagId) {
    Tag findTag = tagRepository.findById(tagId)
        .orElseThrow(() -> new IllegalArgumentException("Not Exsit Tag"));

    findTag.updateTagName(tag.tagName());

    tagRepository.save(findTag);
  }
}
