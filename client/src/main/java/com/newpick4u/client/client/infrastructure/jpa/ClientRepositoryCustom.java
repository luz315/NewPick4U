package com.newpick4u.client.client.infrastructure.jpa;

import com.newpick4u.client.client.application.dto.response.GetClientResponseDto;
import com.newpick4u.client.client.domain.criteria.SearchClientCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ClientRepositoryCustom {

  Page<GetClientResponseDto> getClients(Pageable pageable, SearchClientCriteria criteria);

}
