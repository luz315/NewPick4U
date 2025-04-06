package com.newpick4u.client.client.infrastructure.jpa;

import com.newpick4u.client.client.domain.entity.Client;
import com.newpick4u.client.client.domain.repository.ClientRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class ClientRepositoryImpl implements ClientRepository {

  private final ClientJpaRepository jpaRepository;

  @Override
  public Client save(Client client) {
    return jpaRepository.save(client);
  }

  @Override
  public boolean existsByEmail(String email) {
    return jpaRepository.existsByEmail(email);
  }

  @Override
  public boolean existsByPhone(String phoneNumber) {
    return jpaRepository.existsByPhone(phoneNumber);
  }

  @Override
  public Optional<Client> findById(UUID clientID) {
    return jpaRepository.findById(clientID);
  }
}
