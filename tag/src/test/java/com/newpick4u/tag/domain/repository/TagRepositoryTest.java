//package com.newpick4u.tag.domain.repository;
//
//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
//
//import com.newpick4u.common.config.QueryDslConfig;
//import com.newpick4u.tag.tag.domain.criteria.SearchTagCriteria;
//import com.newpick4u.tag.tag.domain.entity.Tag;
//import com.newpick4u.tag.tag.domain.repository.TagRepository;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.context.annotation.Import;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.test.context.ActiveProfiles;
//
//@ActiveProfiles("test")
//@DataJpaTest
//@Import(QueryDslConfig.class)
//class TagRepositoryTest {
//
//  @Autowired
//  private TagRepository tagRepository;
//
//  @Test
//  void searchByCriteria() {
//    Tag tag1 = Tag.create("축구", 1L);
//    Tag tag2 = Tag.create("농구", 1L);
//    Tag tag3 = Tag.create("야구", 1L);
//    Tag tag4 = Tag.create("배드민턴", 1L);
//
//    tagRepository.save(tag1);
//    tagRepository.save(tag2);
//    tagRepository.save(tag3);
//    tagRepository.save(tag4);
//
//    SearchTagCriteria criteria = new SearchTagCriteria("민", 1L, 1L);
//
//    Pageable pageable = PageRequest.of(0, 10);
//    Page<Tag> tags = tagRepository.searchByCriteria(criteria, pageable);
//
//    assertThat(tags).isNotNull();
//    assertThat(tags.getTotalElements()).isEqualTo(1);
//  }
//
//  @Test
//  void findById() {
//    Tag tag = Tag.create("축구", 1L);
//
//    Tag findTag = tagRepository.save(tag);
//
//    Tag result = tagRepository.findById(findTag.getTagId()).get();
//
//    assertThat(result).isEqualTo(findTag);
//  }
//
//  @Test
//  void save() {
//    Tag tag = Tag.create("축구", 1L);
//
//    Tag savedTag = tagRepository.save(tag);
//
//    assertThat(savedTag.getTagId()).isNotNull();
//    assertThat(savedTag.getTagName()).isEqualTo("축구");
//    assertThat(savedTag.getScore()).isEqualTo(1L);
//  }
//}