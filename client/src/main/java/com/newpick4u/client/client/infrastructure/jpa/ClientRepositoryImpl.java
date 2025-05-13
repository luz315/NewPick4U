package com.newpick4u.client.client.infrastructure.jpa;

import com.newpick4u.client.client.application.dto.response.GetClientResponseDto;
import com.newpick4u.client.client.domain.criteria.SearchClientCriteria;
import com.newpick4u.client.client.domain.entity.Client;
import com.newpick4u.client.client.domain.repository.ClientRepository;
import com.newpick4u.client.client.infrastructure.elasticsearch.ClientSearchRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class ClientRepositoryImpl implements ClientRepository {

  private final ClientJpaRepository jpaRepository;
  private final ClientRepositoryCustom clientRepositoryCustom;
  private final ClientSearchRepository searchRepository;

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

  @Override
  public Page<GetClientResponseDto> getClients(Pageable pageable, SearchClientCriteria criteria) {
    return clientRepositoryCustom.getClients(pageable, criteria);
  }
}
