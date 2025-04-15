package com.newpick4u.user.application.usecase;

import com.newpick4u.user.application.dto.request.CreateUserRequestDto;
import com.newpick4u.user.application.dto.request.PointUpdateMessage;
import com.newpick4u.user.application.dto.request.SignInUserRequestDto;
import com.newpick4u.user.application.dto.response.GetUserResponseDto;
import com.newpick4u.user.application.dto.response.SignInUserResponseDto;

public interface UserService {

  Long createUser(CreateUserRequestDto request);

  SignInUserResponseDto signInUser(SignInUserRequestDto request);

  GetUserResponseDto getUser(Long userId);

  void updatePoint(PointUpdateMessage request);
}
