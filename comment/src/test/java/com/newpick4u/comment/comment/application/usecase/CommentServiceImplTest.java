package com.newpick4u.comment.comment.application.usecase;

import com.newpick4u.comment.comment.application.NewsClient;
import com.newpick4u.comment.comment.application.ThreadClient;
import com.newpick4u.comment.comment.infrastructure.jpa.CommentJpaRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@Slf4j
@ActiveProfiles("test")
@SpringBootTest
class CommentServiceImplTest {

  @Autowired
  CommentServiceImpl commentService;

  @MockitoBean
  NewsClient newsClient;

  @MockitoBean
  ThreadClient threadClient;

  @Autowired
  CommentJpaRepository commentJpaRepository;

  @Autowired
  RedisTemplate<String, String> redisTemplate;
  @Autowired
  private CommentServiceImpl commentServiceImpl;

  @Test
  @DisplayName("뉴스-댓글 생성 테스트")
  void saveCommentForNewsTest() {
//    ArrayList<UUID> saveReqNewsIdList = new ArrayList<>();
//    List<String> tagList = List.of("경제", "삼성전자", "주가");
//
//    // given
//    for (int i = 0; i < 3; i++) {
//      String newsIdString = "00000000-0000-0000-0000-00000000000" + i;
//      UUID newsId = UUID.fromString(newsIdString);
//      saveReqNewsIdList.add(newsId);
//      Mockito.when(newsClient.isExistNews(newsId)).thenReturn(true);
//
//      UUID advertisementId = UUID.fromString(newsIdString);
//      CommentSaveRequestDto requestDto = new CommentSaveRequestDto(true, advertisementId, null,
//          newsId,
//          "이것은 첫번째 댓글입니다.",
//          tagList);
//
//      // when
//      UUID uuid = commentService.saveCommentForNews(
//          requestDto,
//          CurrentUserInfoDto.of(Long.valueOf(Integer.valueOf(i).toString()), UserRole.ROLE_USER)
//      );
//    }
//
//    // then
//    // RDB 조회
//    List<UUID> saveResultNewsIdList = commentJpaRepository.findAll()
//        .stream()
//        .map(Comment::getNewsId)
//        .toList();
//    Assertions.assertEquals(saveReqNewsIdList.size(), saveResultNewsIdList.size());
//    for (UUID saveReqNewsId : saveReqNewsIdList) {
//      Assertions.assertTrue(saveResultNewsIdList.contains(saveReqNewsId));
//    }
//
//    // 캐싱 조회
//    String TAG_COUNT_ZSET_KEY = "tag_count";
//    String TAG_TTL_KEY_PREFIX = "tag:ttl:";
//    ZSetOperations<String, String> zSetOperations = redisTemplate.opsForZSet();
//    ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
//    for (String tagName : tagList) {
//      // zSet 캐시 조회
//      Double score = zSetOperations.score(TAG_COUNT_ZSET_KEY, tagName);
//      Assertions.assertNotNull(score);
//      Long scoreLong = Long.valueOf(score.longValue());
//      Assertions.assertEquals(Long.valueOf(saveReqNewsIdList.size()), scoreLong);
//
//      // TTL value 조회
//      String savedTTL = valueOperations.get(TAG_TTL_KEY_PREFIX + tagName);
//      Assertions.assertNotNull(savedTTL);
//
//      Long expire = redisTemplate.getExpire(TAG_TTL_KEY_PREFIX + tagName, TimeUnit.SECONDS);
//      log.info("tagName={} : expire={}", tagName, expire);
//    }

    // 카프카는 local 확인
  }

//  @Test
//  @DisplayName("쓰레드-댓글 생성 테스트")
//  void saveCommentForThreadTest() {
//    ArrayList<UUID> saveReqThreadIdList = new ArrayList<>();
//    List<String> tagList = List.of("경제", "삼성전자", "주가");
//
//    // given
//    for (int i = 0; i < 3; i++) {
//      String threadIdString = "00000000-0000-0000-0000-00000000000" + i;
//      UUID threadId = UUID.fromString(threadIdString);
//      saveReqThreadIdList.add(threadId);
//      Mockito.when(threadClient.isExistThread(threadId)).thenReturn(true);
//
//      CommentSaveRequestDto requestDto = new CommentSaveRequestDto(false, null, threadId,
//          null,
//          "이것은 첫번째 댓글입니다.",
//          tagList);
//
//      // when
//      UUID uuid = commentService.saveCommentForThread(
//          requestDto,
//          CurrentUserInfoDto.of(Long.valueOf(Integer.valueOf(i).toString()), UserRole.ROLE_USER)
//      );
//    }
//
//    // then
//    // RDB 조회
//    List<UUID> saveResultThreadIdList = commentJpaRepository.findAll()
//        .stream()
//        .map(Comment::getThreadId)
//        .toList();
//    Assertions.assertEquals(saveReqThreadIdList.size(), saveResultThreadIdList.size());
//    for (UUID saveReqThreadId : saveReqThreadIdList) {
//      Assertions.assertTrue(saveResultThreadIdList.contains(saveReqThreadId));
//    }
//  }
//
//  @Test
//  @DisplayName("쓰레드의 댓글 목록 조회 테스트")
//  void getCommentByThreadIdTest() {
//    // given
//    int commentCount = 3;
//    UUID threadId = UUID.randomUUID();
//    for (int i = 0; i < commentCount; i++) {
//      commentJpaRepository.save(Comment.createForThread(threadId, "이것은 쓰레드의 댓글 " + i + " 입니다."));
//    }
//
//    GetCommentListForThreadResponseDto commentByThreadId = commentServiceImpl.getCommentByThreadId(
//        threadId);
//    Assertions.assertEquals(threadId, commentByThreadId.threadId());
//    Assertions.assertEquals(commentCount, commentByThreadId.commentList().size());
//    for (String content : commentByThreadId.commentList()) {
//      log.info("thread={}, result = {}", threadId, content);
//    }
//  }
}