package com.newpick4u.client.advertisement.application.client;

import com.newpick4u.client.advertisement.application.dto.response.GetNewsResponseDto;
import com.newpick4u.common.resolver.dto.CurrentUserInfoDto;
import com.newpick4u.common.response.ApiResponse;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

public interface NewsClient {

  ResponseEntity<ApiResponse<GetNewsResponseDto>> getNews(@PathVariable("newsId") UUID newsId,
      CurrentUserInfoDto userInfoDto);

}
