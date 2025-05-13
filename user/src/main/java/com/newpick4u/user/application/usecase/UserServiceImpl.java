package com.newpick4u.user.application.usecase;

import com.newpick4u.user.application.dto.request.CreateUserRequestDto;
import com.newpick4u.user.application.dto.request.PointUpdateMessage;
import com.newpick4u.user.application.dto.request.SignInUserRequestDto;
import com.newpick4u.user.application.dto.response.GetUserResponseDto;
import com.newpick4u.user.application.dto.response.SignInUserResponseDto;
import com.newpick4u.user.application.exception.UserException;
import com.newpick4u.user.application.exception.UserException.NotFoundException;
import com.newpick4u.user.domain.entity.User;
import com.newpick4u.user.domain.repository.UserRepository;
import com.newpick4u.user.global.common.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final BCryptPasswordEncoder passwordEncoder;
  private final JwtProvider jwtProvider;

  @Override
  public Long createUser(CreateUserRequestDto request) {
    if (isExistsUsername(request)) {
      throw new UserException.AlreadyExistsUserNameException();
    }
    User user = User.create(request.username(), passwordEncoder.encode(request.password()),
        request.name());
    User savedUser = userRepository.save(user);
    return savedUser.getId();
  }

  @Override
  public SignInUserResponseDto signInUser(SignInUserRequestDto request) {
    User user = getUserByUsername(request.username());
    if (isInvalidPassword(request.password(), user.getPassword())) {
      throw new UserException.InvalidPasswordException();
    }

    String accessToken = jwtProvider.createAccessToken(user.getId(), user.getRole());
    return SignInUserResponseDto.of(accessToken, user.getId());
  }

  @Override
  public GetUserResponseDto getUser(Long userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(NotFoundException::new);
    return GetUserResponseDto.from(user);
  }

  @Transactional
  @Override
  public void updatePoint(PointUpdateMessage request) {
    User user = userRepository.findById(request.userId())
        .orElseThrow(NotFoundException::new);
    user.addPoint(request.point());
  }

  private User getUserByUsername(String username) {
    return userRepository.findUserByUsername(username)
        .orElseThrow(UserException.NotFoundException::new);
  }


  private boolean isExistsUsername(CreateUserRequestDto request) {
    return userRepository.existsByUsername(request.username());
  }

  private boolean isInvalidPassword(String rawPassword, String encodedPassword) {
    return !passwordEncoder.matches(rawPassword, encodedPassword);
  }
}
