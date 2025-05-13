package com.newpick4u.client.client.application.usecase;

import com.newpick4u.client.client.application.dto.request.CreateClientRequestDto;
import com.newpick4u.client.client.application.dto.request.UpdateClientRequestDto;
import com.newpick4u.client.client.application.dto.response.GetClientResponseDto;
import com.newpick4u.client.client.domain.criteria.SearchClientCriteria;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ClientService {

  public UUID saveClient(CreateClientRequestDto request);

  public UUID updateClient(UUID clientId, UpdateClientRequestDto request);

  public UUID deleteClient(UUID clientId, Long deletedBy);

  public GetClientResponseDto getClient(UUID clientId);

  public Page<GetClientResponseDto> getClients(Pageable pageable, SearchClientCriteria criteria);
}
