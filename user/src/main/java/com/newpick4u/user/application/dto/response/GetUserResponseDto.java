package com.newpick4u.user.application.dto.response;

import com.newpick4u.user.domain.entity.User;
import java.io.Serializable;

public record GetUserResponseDto(Long userId, String username, String name, String role) implements
    Serializable {

  public static GetUserResponseDto from(User user) {
    return new GetUserResponseDto(user.getId(), user.getUsername(), user.getName(),
        user.getRole().toString());
  }


}
