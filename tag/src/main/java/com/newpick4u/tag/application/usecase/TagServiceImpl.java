package com.newpick4u.tag.application.usecase;

import com.newpick4u.common.exception.CustomException;
import com.newpick4u.common.exception.type.ApiErrorCode;
import com.newpick4u.tag.application.dto.AiNewsDto;
import com.newpick4u.tag.application.dto.NewsTagDto.TagDto;
import com.newpick4u.tag.application.dto.UpdateTagRequestDto;
import com.newpick4u.tag.domain.criteria.SearchTagCriteria;
import com.newpick4u.tag.domain.entity.Tag;
import com.newpick4u.tag.domain.repository.TagRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TagServiceImpl implements TagService {

  private final TagRepository tagRepository;

  @Override
  public Page<Tag> getTags(SearchTagCriteria criteria, Pageable pageable) {
    return tagRepository.searchByCriteria(criteria, pageable);
  }

  @Override
  @Transactional
  public UpdateTagRequestDto updateTag(UpdateTagRequestDto tag, UUID tagId) {
    Tag findTag = tagRepository.findById(tagId)
        .orElseThrow(() -> new CustomException(ApiErrorCode.NOT_FOUND));

    findTag.updateTagName(tag.tagName());

    Tag save = tagRepository.save(findTag);

    return new UpdateTagRequestDto(save.getTagName());
  }

  @Override
  @Transactional
  public List<TagDto> createTagFromAi(AiNewsDto dto) {
    List<TagDto> tagList = new ArrayList<>();

    dto.tags().forEach(tagName -> {
      Optional<Tag> tag = tagRepository.findByTagName(tagName); // 존재하는 태그는 score +1 처리
      if (tag.isPresent()) {
        Tag existingTag = tag.get();
        existingTag.increaseScore();
        TagDto tagDto = new TagDto(existingTag.getId(), tagName);
        tagList.add(tagDto);
        return;
      }

      // 존재하지 않는 태그의 경우는 새로 생성해서 db에 저장
      Tag newTag = Tag.create(tagName);
      Tag savedTag = tagRepository.save(newTag);
      TagDto tagDto = new TagDto(savedTag.getId(), tagName);
      tagList.add(tagDto);
    });

    return tagList;
  }

  @Override
  @Transactional
  public void deleteTagFromAi(AiNewsDto dto) {
    dto.tags().forEach(tagName -> {
      Tag tag = tagRepository.findByTagName(tagName)
          .orElseThrow(() -> new IllegalArgumentException("해당 태그가 존재하지 않습니다."));

      if (tag.getScore() <= 1) {
        tagRepository.delete(tag);
      } else {
        tag.decreaseScore();
      }

    });
  }
}
