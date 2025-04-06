package com.newpick4u.client.client.infrastructure.jpa;

import com.newpick4u.client.client.domain.entity.Client;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientJpaRepository extends JpaRepository<Client, UUID> {

  boolean existsByEmail(String email);

  boolean existsByPhone(String phoneNumber);
}
