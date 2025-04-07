package com.newpick4u.thread.thread.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.newpick4u.thread.thread.application.dto.ThreadResponseDto;
import com.newpick4u.thread.thread.domain.entity.Thread;
import com.newpick4u.thread.thread.domain.repository.ThreadRepository;
import com.newpick4u.thread.thread.infrastructure.client.CommentClient;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class ThreadServiceImplTest {

  @InjectMocks
  private ThreadServiceImpl threadService;

  @Mock
  private ThreadRepository threadRepository;

  @Mock
  private CommentClient commentClient;

  @Test
  @DisplayName("쓰레드 전체 조회 테스트")
  void getThreads() {
    // given
    UUID threadId1 = UUID.randomUUID();
    UUID threadId2 = UUID.randomUUID();
    UUID newsId1 = UUID.randomUUID();
    UUID newsId2 = UUID.randomUUID();

    Thread thread1 = Thread.create(newsId1, "summary1");
    Thread thread2 = Thread.create(newsId2, "summary2");
    ReflectionTestUtils.setField(thread1, "id", threadId1);
    ReflectionTestUtils.setField(thread2, "id", threadId2);

    List<Thread> threads = List.of(thread1, thread2);
    Pageable pageable = PageRequest.of(0, 10);
    Page<Thread> threadPage = new PageImpl<>(threads, pageable, threads.size());

    Map<UUID, Long> commentCountMap = Map.of(
        threadId1, 5L,
        threadId2, 3L
    );

    when(threadRepository.findAll(pageable)).thenReturn(threadPage);
    when(commentClient.getCommentCountMap(anyList())).thenReturn(commentCountMap);

    // when
    Page<ThreadResponseDto> result = threadService.getThreads(pageable);

    // then
    assertThat(result.getTotalElements()).isEqualTo(2);
    assertThat(result.getContent()).hasSize(2);

    ThreadResponseDto dto1 = result.getContent().get(0);
    ThreadResponseDto dto2 = result.getContent().get(1);

    assertThat(dto1.threadId()).isEqualTo(threadId1);
    assertThat(dto1.commentCount()).isEqualTo(5L);

    assertThat(dto2.threadId()).isEqualTo(threadId2);
    assertThat(dto2.commentCount()).isEqualTo(3L);

    verify(threadRepository).findAll(pageable);
    verify(commentClient).getCommentCountMap(List.of(threadId1, threadId2));
  }

  @Test
  @DisplayName("쓰레드 상세 조회")
  void getThreadDetail() {
    // given
    UUID threadId = UUID.randomUUID();
    UUID newsId = UUID.randomUUID();
    Thread thread = Thread.create(newsId, "summary1");
    ReflectionTestUtils.setField(thread, "id", threadId);

    when(threadRepository.findById(threadId)).thenReturn(Optional.of(thread));

    // when
    Thread result = threadService.getThreadDetail(threadId);

    // then
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(threadId);

    verify(threadRepository).findById(threadId);
  }
}