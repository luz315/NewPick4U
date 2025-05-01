package com.newpick4u.thread.thread.application.usecase;

import com.newpick4u.common.response.SliceResponse;
import com.newpick4u.thread.thread.application.dto.ThreadResponseDto;
import com.newpick4u.thread.thread.domain.entity.Thread;
import java.util.UUID;
import org.springframework.data.domain.Pageable;

public interface ThreadService {

  SliceResponse<ThreadResponseDto> getThreads(Pageable pageable);

  Thread getThreadDetail(UUID threadId);

  Boolean existThread(UUID threadId);
}
