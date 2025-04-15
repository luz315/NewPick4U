package com.newpick4u.user.application.dto.request;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateUserRequestDto(@NotNull @Size(min = 4, max = 50) String username,
                                   @NotNull @Size(min = 4, max = 100) String password,
                                   @NotNull @Size(min = 4, max = 50) String name) {

}
