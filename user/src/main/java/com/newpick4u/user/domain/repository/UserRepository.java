package com.newpick4u.user.domain.repository;

import com.newpick4u.user.domain.entity.User;
import java.util.Optional;

public interface UserRepository {

  User save(User user);

  boolean existsByUsername(String userName);

  Optional<User> findUserByUsername(String userName);

  Optional<User> findById(Long userId);
}
