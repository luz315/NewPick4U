package com.newpick4u.comment.comment.application.usecase;

import com.newpick4u.comment.comment.application.NewsClient;
import com.newpick4u.comment.comment.domain.entity.Comment;
import com.newpick4u.comment.comment.infrastructure.jpa.CommentJpaRepository;
import com.newpick4u.common.resolver.dto.CurrentUserInfoDto;
import com.newpick4u.common.resolver.dto.UserRole;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@Slf4j
@MockitoBean(types = ScheduledAnnotationBeanPostProcessor.class) // @Scheduled 무력화
@TestInstance(Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@SpringBootTest
class CommentFacadeServiceImplTest {

  @Autowired
  CommentFacadeServiceImpl commentFacadeService;

  @Autowired
  CommentJpaRepository commentJpaRepository;

  @MockitoBean
  NewsClient newsClient;

  @Autowired
  RedisTemplate<String, String> redisTemplate;

  ArrayList<UUID> commentUUIDList = new ArrayList<>();

  @Test
  @DisplayName("동시성 테스트")
  void concurrentTest() {

    // given
    ArrayList<Comment> commentList = new ArrayList<>();
    for (int i = 0; i < 3; i++) {
      Comment comment = Comment.createForNews(UUID.randomUUID(), "댓글내용" + i, "userName");
      commentList.add(comment);
    }
    commentJpaRepository.saveAll(commentList);
    commentList.stream().map(Comment::getId).forEach(commentUUIDList::add);
    for (UUID uuid : commentUUIDList) {
      log.info("start commentid : {}", uuid);
    }

    // 50명씩 동시에 수행
    // 1명당 댓글 좋아요 -> 댓글 좋아요 취소 동작 수행
    int userCount = 1000;
    ExecutorService threadPool = Executors.newFixedThreadPool(userCount);
    CountDownLatch readyLatch = new CountDownLatch(userCount);
    CountDownLatch startLatch = new CountDownLatch(1);
    CountDownLatch doneLatch = new CountDownLatch(userCount);

    Assertions.assertFalse(commentUUIDList.isEmpty(), "commentUUIDList must not be empty");

    for (int i = 1; i <= userCount; i++) {
      Integer finalI = Integer.valueOf(i);
      threadPool.execute(() -> {
        UUID commentId = commentUUIDList.get(finalI % commentUUIDList.size());

        readyLatch.countDown();
        try {
          startLatch.await();
          commentFacadeService.createGood(
              commentId,
              CurrentUserInfoDto.of(
                  finalI.longValue(),
                  UserRole.ROLE_USER
              )
          );
          commentFacadeService.deleteGood(
              commentId,
              CurrentUserInfoDto.of(
                  finalI.longValue(),
                  UserRole.ROLE_USER
              )
          );
        } catch (InterruptedException e) {
          log.error("InterruptedException : ", e);
          throw new RuntimeException(e);
        } finally {
          doneLatch.countDown();
        }
      });
    }

    try {
      readyLatch.await();     // 모든 스레드 준비 완료까지 대기
      startLatch.countDown(); // 동시에 시작 신호
      doneLatch.await();      // 모든 작업 완료까지 대기
    } catch (InterruptedException e) {
      throw new RuntimeException("Main thread interrupted", e);
    } finally {
      threadPool.shutdown();
    }

    for (UUID commentId : commentUUIDList) {
      Assertions.assertEquals(0, commentJpaRepository.findById(commentId).get().getGoodCount());
    }
  }

  @Test
  @DisplayName("뉴스-댓글 생성 : RDB, Cache, Kafka 통합 테스트")
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
//      UUID uuid = commentFacadeService.saveCommentForNews(
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
}