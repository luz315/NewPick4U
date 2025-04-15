package com.newpick4u.user.application.dto.request;

import jakarta.validation.constraints.NotNull;

public record SignInUserRequestDto(@NotNull String username, @NotNull String password) {

}
