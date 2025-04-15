package com.newpick4u.thread.thread.application.usecase;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.newpick4u.thread.thread.domain.entity.Thread;
import com.newpick4u.thread.thread.domain.entity.ThreadStatus;
import com.newpick4u.thread.thread.domain.repository.ThreadRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

@ExtendWith(MockitoExtension.class)
class ThreadServiceImplTest {

  @Mock
  private RedisTemplate<String, String> redisTemplate;

  @InjectMocks
  private ThreadServiceImpl threadService;

  @Mock
  private ThreadRepository threadRepository;

  @Mock
  private ZSetOperations<String, String> zSetOperations;

  @Test
  @DisplayName("핫태그가 존재하고 open된 쓰레드가 없을 때, 쓰레드를 생성한다")
  void generate_thread_test_1() throws Exception {
    //given
    Set<ZSetOperations.TypedTuple<String>> redisHotTags = Set.of(
        createTuple("AI", 50.0),
        createTuple("IT", 45.0),
        createTuple("게임", 40.0)
    );
    when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
    when(zSetOperations.reverseRangeByScoreWithScores("tag:comment:score", 30, Double.MAX_VALUE))
        .thenReturn(redisHotTags);

    when(threadRepository.findAllByStatus(ThreadStatus.OPEN))
        .thenReturn(Collections.emptyList());

    when(threadRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

    // when
    threadService.saveThread();

    // then
    verify(threadRepository, times(3)).save(any(Thread.class));
  }

  @Test
  @DisplayName("핫태그 중 이미 존재하는 태그가 있을 경우, 해당 쓰레드의 score만 증가")
  void generate_thread_test_2() throws Exception {
    // given
    String existingTag = "AI";
    Thread existingThread = mock(Thread.class);

    Set<ZSetOperations.TypedTuple<String>> redisHotTags = Set.of(
        createTuple(existingTag, 50.0)
    );

    when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
    when(zSetOperations.reverseRangeByScoreWithScores("tag:comment:score", 30, Double.MAX_VALUE))
        .thenReturn(redisHotTags);

    when(threadRepository.findAllByStatus(ThreadStatus.OPEN))
        .thenReturn(List.of(existingThread));
    when(existingThread.getTagName()).thenReturn(existingTag);

    // when
    threadService.saveThread();

    // then
    verify(existingThread).plusScore();                  // score 증가 메서드 호출 확인
    verify(threadRepository).save(existingThread);       // 업데이트 저장 확인
    verify(threadRepository, times(1)).save(any());      // 새 쓰레드 생성이 아님을 확인
  }


  @Test
  @DisplayName("open된 쓰레드가 5개 이상이고 핫태그에 해당하지 않는 경우, 가장 score 낮은 쓰레드를 종료 후 새 쓰레드 생성")
  void generate_thread_test_3() {
    // given
    List<Thread> openThreads = new ArrayList<>();
    for (int i = 0; i < 5; i++) {
      Thread thread = mock(Thread.class);
      when(thread.getTagName()).thenReturn("oldTag" + i);
      when(thread.getScore()).thenReturn((long) (50 + i));  // 점수 50~54
      openThreads.add(thread);
    }

    // 가장 낮은 score를 가진 쓰레드로 가정
    when(openThreads.get(0).getScore()).thenReturn(30L);

    Set<ZSetOperations.TypedTuple<String>> redisHotTags = Set.of(
        createTuple("newHotTag", 70.0)  // 기존 쓰레드와 겹치지 않음
    );

    when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
    when(zSetOperations.reverseRangeByScoreWithScores("tag:comment:score", 30, Double.MAX_VALUE))
        .thenReturn(redisHotTags);

    when(threadRepository.findAllByStatus(ThreadStatus.OPEN)).thenReturn(openThreads);

    // when
    threadService.saveThread();

    // then
    verify(openThreads.get(0)).closedThread();           // 가장 낮은 쓰레드 종료
    verify(threadRepository).save(openThreads.get(0));   // 상태 변경 저장
    verify(threadRepository).save(argThat(thread ->
        thread.getTagName().equals("newHotTag")));        // 새 쓰레드 저장
  }

  private ZSetOperations.TypedTuple<String> createTuple(String value, double score) {
    return new DefaultTypedTuple<>(value, score);
  }
}