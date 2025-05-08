package com.newpick4u.client.client.application.usecase;

import com.newpick4u.client.client.application.dto.request.SearchClientRequestDto;
import com.newpick4u.client.client.domain.entity.ClientDocument;
import com.newpick4u.common.response.PageResponse;
import org.springframework.data.domain.Pageable;

public interface ClientSearchService {

  public PageResponse<ClientDocument> search(SearchClientRequestDto request, Pageable pageable);
}
