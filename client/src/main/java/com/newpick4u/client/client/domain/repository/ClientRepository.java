package com.newpick4u.client.client.domain.repository;

import com.newpick4u.client.client.application.dto.response.GetClientResponseDto;
import com.newpick4u.client.client.domain.criteria.SearchClientCriteria;
import com.newpick4u.client.client.domain.entity.Client;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ClientRepository {

  Client save(Client client);

  boolean existsByEmail(String email);

  boolean existsByPhone(String phoneNumber);

  Optional<Client> findById(UUID clientID);

  Page<GetClientResponseDto> getClients(Pageable pageable, SearchClientCriteria criteria);
}
