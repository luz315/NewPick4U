//package com.newpick4u.tag.application.usecase;
//
//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
//import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
//
//import com.newpick4u.tag.tag.application.dto.UpdateTagRequestDto;
//import com.newpick4u.tag.tag.application.usecase.TagServiceImpl;
//import com.newpick4u.tag.tag.domain.criteria.SearchTagCriteria;
//import com.newpick4u.tag.tag.domain.entity.Tag;
//import com.newpick4u.tag.tag.domain.repository.TagRepository;
//import java.util.List;
//import java.util.Optional;
//import java.util.UUID;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.test.util.ReflectionTestUtils;
//
//@ExtendWith(MockitoExtension.class)
//class TagServiceImplTest {
//
//  @InjectMocks
//  private TagServiceImpl tagService;
//
//  @Mock
//  private TagRepository tagRepository;
//
//
//  private final UUID TAG_ID = UUID.randomUUID();
//  private final Tag EXISTING_TAG = Tag.create("기존태그", 10L);
//
//  @Test
//  void searchTest() {
//    // given
//    SearchTagCriteria criteria = SearchTagCriteria.builder()
//        .tagName("테스트")
//        .minScore(1L)
//        .maxScore(10L)
//        .build();
//
//    Pageable pageable = PageRequest.of(0, 10);
//    List<Tag> tags = List.of(Tag.create("테스트태그", 5L));
//    Page<Tag> tagPage = new PageImpl<>(tags);
//
//    Mockito.when(tagRepository.searchByCriteria(criteria, pageable))
//        .thenReturn(tagPage);
//
//    // when
//    Page<Tag> result = tagService.getTags(criteria, pageable);
//
//    // then
//    assertThat(result.getTotalElements()).isEqualTo(1);
//    assertThat(result.getContent().get(0).getTagName()).isEqualTo("테스트태그");
//  }
//
//  @Test
//  void updateTest() {
//    // given
//    UpdateTagRequestDto dto = new UpdateTagRequestDto("수정된태그");
//
//    Tag savedTag = Tag.create("이전태그", 5L);
//    ReflectionTestUtils.setField(savedTag, "tagId", TAG_ID); // private 필드 세팅
//
//    Mockito.when(tagRepository.findById(TAG_ID))
//        .thenReturn(Optional.of(savedTag));
//
//    // when
//    tagService.updateTag(dto, TAG_ID);
//
//    // then
//    assertThat(savedTag.getTagName()).isEqualTo("수정된태그");
//    Mockito.verify(tagRepository).save(savedTag);
//  }
//
//  @Test
//  void notExistTagTest() {
//    // given
//    UpdateTagRequestDto dto = new UpdateTagRequestDto("수정된태그");
//
//    Mockito.when(tagRepository.findById(TAG_ID))
//        .thenReturn(Optional.empty());
//
//    // when & then
//    assertThatThrownBy(() -> tagService.updateTag(dto, TAG_ID))
//        .isInstanceOf(IllegalArgumentException.class)
//        .hasMessage("Not Exsit Tag");
//  }
//}