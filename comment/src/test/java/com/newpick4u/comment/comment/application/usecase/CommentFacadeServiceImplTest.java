package com.newpick4u.comment.comment.application.usecase;

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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@Slf4j
@TestInstance(Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@SpringBootTest
class CommentFacadeServiceImplTest {

  @Autowired
  CommentFacadeServiceImpl commentFacadeService;

  @Autowired
  CommentJpaRepository commentJpaRepository;

  ArrayList<UUID> commentUUIDList = new ArrayList<>();

  @BeforeAll
  void init() {
    ArrayList<Comment> commentList = new ArrayList<>();
    for (int i = 0; i < 3; i++) {
      Comment comment = Comment.createForNews(UUID.randomUUID(), "댓글내용" + i);
      commentList.add(comment);
    }
    commentJpaRepository.saveAll(commentList);
    commentList.stream().map(Comment::getId).forEach(commentUUIDList::add);
    for (UUID uuid : commentUUIDList) {
      log.info("start commentid : {}", uuid);
    }
  }

  @Test
  @DisplayName("동시성 테스트")
  void concurrentTest() {
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
}