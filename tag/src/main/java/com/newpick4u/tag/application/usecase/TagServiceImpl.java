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
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
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
    List<String> tagNames = dto.tags();

    // 1) 한 번에 기존 태그들 조회
    List<Tag> existingTags = tagRepository.findAllByTagNameIn(tagNames);
    Set<String> existingNames = existingTags.stream()
        .map(Tag::getTagName)
        .collect(Collectors.toSet());

    // 2) 배치 SQL로 점수 증분
    if (!existingNames.isEmpty()) {
      tagRepository.incrementScoreByTagNames(new ArrayList<>(existingNames));
    }

    // 3) 기존 태그 DTO 변환
    List<TagDto> tagList = existingTags.stream()
        .map(t -> new TagDto(t.getId(), t.getTagName()))
        .collect(Collectors.toList());

    // 4) 신규 태그만 걸러내서 배치 생성
    List<Tag> newTags = tagNames.stream()
        .filter(name -> !existingNames.contains(name))
        .distinct()
        .map(Tag::create)
        .collect(Collectors.toList());

    List<Tag> savedNewTags = Collections.emptyList();
    if (!newTags.isEmpty()) {
      savedNewTags = tagRepository.saveAll(newTags);
      tagList.addAll(savedNewTags.stream()
          .map(t -> new TagDto(t.getId(), t.getTagName()))
          .toList());
    }

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
