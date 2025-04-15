package com.newpick4u.user.infrastructure.jpa;

import com.newpick4u.user.domain.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<User, Long> {

  boolean existsByUsername(String userName);

  Optional<User> findByUsername(String userName);

}
