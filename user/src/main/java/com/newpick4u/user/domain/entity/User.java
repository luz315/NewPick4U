package com.newpick4u.user.domain.entity;

import com.newpick4u.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "p_user")
@Entity
public class User extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "user_id")
  private Long id;

  @Column(name = "username", length = 50, nullable = false, unique = true)
  private String username;

  @Column(length = 100, nullable = false)
  private String password;

  @Column(length = 100, nullable = false)
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private UserRole role;
  @Column(nullable = false)
  private Integer point;

  @Builder(access = AccessLevel.PRIVATE)
  private User(String username, String password, String name, UserRole role) {
    this.username = username;
    this.password = password;
    this.name = name;
    this.role = UserRole.ROLE_USER;
    this.point = 0;
  }

  public static User create(String username, String password, String name) {
    return User.builder()
        .username(username)
        .password(password)
        .name(name)
        .build();
  }

  public void addPoint(Integer point) {
    this.point += point;
  }

  @Getter
  @RequiredArgsConstructor
  public enum UserRole {
    ROLE_MASTER("관리자"),
    ROLE_USER("회원");

    private final String description;
  }
}
