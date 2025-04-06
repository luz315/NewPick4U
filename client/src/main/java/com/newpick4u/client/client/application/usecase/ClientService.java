package com.newpick4u.client.client.application.usecase;

import com.newpick4u.client.client.application.dto.request.CreateClientRequestDto;
import com.newpick4u.client.client.application.dto.request.UpdateClientRequestDto;
import java.util.UUID;

public interface ClientService {

  public UUID saveClient(CreateClientRequestDto request);

  public UUID updateClient(UUID clientId, UpdateClientRequestDto request);

  public UUID deleteClient(UUID clientId, Long deletedBy);
}
