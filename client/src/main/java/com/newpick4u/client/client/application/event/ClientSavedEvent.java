package com.newpick4u.client.client.application.event;

import com.newpick4u.client.client.domain.entity.Client;
import java.util.UUID;

public record ClientSavedEvent(
    UUID id,
    String name,
    String address,
    String email,
    String phone,
    String industry
) {

  public static ClientSavedEvent from(Client client) {
    return new ClientSavedEvent(
        client.getClientId(),
        client.getName(),
        client.getAddress(),
        client.getEmail(),
        client.getPhone(),
        client.getIndustry().name()
    );
  }
}
