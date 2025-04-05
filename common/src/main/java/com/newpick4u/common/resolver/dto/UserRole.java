package com.newpick4u.common.resolver.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserRole {
  ROLE_MASTER("관리자"),
  ROLE_USER("회원");

  private final String description;
}
