package com.newpick4u.user.infrastructure;

import com.newpick4u.user.domain.entity.User;
import com.newpick4u.user.domain.repository.UserRepository;
import com.newpick4u.user.infrastructure.jpa.UserJpaRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

  private final UserJpaRepository userJpaRepository;

  @Override
  public User save(User user) {
    return userJpaRepository.save(user);
  }

  @Override
  public boolean existsByUsername(String userName) {
    return userJpaRepository.existsByUsername(userName);
  }

  @Override
  public Optional<User> findUserByUsername(String userName) {
    return userJpaRepository.findByUsername(userName);
  }

  @Override
  public Optional<User> findById(Long userId) {
    return userJpaRepository.findById(userId);
  }
}
