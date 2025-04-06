package com.newpick4u.client.client.domain.repository;

import com.newpick4u.client.client.domain.entity.Client;
import java.util.Optional;
import java.util.UUID;

public interface ClientRepository {

  Client save(Client client);

  boolean existsByEmail(String email);

  boolean existsByPhone(String phoneNumber);

  Optional<Client> findById(UUID clientID);
}
