package com.newpick4u.thread.thread.application.usecase;

import com.newpick4u.thread.thread.application.dto.ThreadResponseDto;
import com.newpick4u.thread.thread.application.exception.ThreadException.NotFoundException;
import com.newpick4u.thread.thread.domain.entity.Thread;
import com.newpick4u.thread.thread.domain.repository.ThreadRepository;
import com.newpick4u.thread.thread.infrastructure.client.CommentClient;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ThreadServiceImpl implements ThreadService {

  private final ThreadRepository threadRepository;
  private final CommentClient commentClient;

  @Override
  @Transactional(readOnly = true)
  public Page<ThreadResponseDto> getThreads(Pageable pageable) {
    Page<Thread> threadList = threadRepository.findAll(pageable);

    List<UUID> threadIds = threadList.getContent()
        .stream()
        .map(Thread::getId)
        .toList();

    Map<UUID, Long> commentCountMap = commentClient.getCommentCountMap(threadIds);

    Page<ThreadResponseDto> response = threadList.map(thread -> {
      Long count = Optional.ofNullable(commentCountMap.get(thread.getId())).orElse(0L);
      return ThreadResponseDto.from(thread, count);
    });

    return response;
  }

  @Override
  @Transactional(readOnly = true)
  public Thread getThreadDetail(UUID threadId) {
    return threadRepository.findById(threadId).orElseThrow(NotFoundException::new);
  }
}
