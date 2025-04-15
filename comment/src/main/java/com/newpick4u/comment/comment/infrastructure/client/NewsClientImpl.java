package com.newpick4u.comment.comment.infrastructure.client;

import com.newpick4u.comment.comment.application.NewsClient;
import com.newpick4u.comment.comment.infrastructure.client.dto.GetNewsResponseDto;
import com.newpick4u.common.response.ApiResponse;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class NewsClientImpl implements NewsClient {

  private final NewsFeignClient newsFeignClient;

  @Override
  public boolean isExistNews(UUID newsId) {
    ResponseEntity<ApiResponse<GetNewsResponseDto>> responseEntity = newsFeignClient.getNewsById(
        newsId);

    if (responseEntity.getStatusCode().is4xxClientError()) {
      return false;
    }

    GetNewsResponseDto data = Objects.requireNonNull(responseEntity.getBody()).data();
    return data.isExist();
  }
}
