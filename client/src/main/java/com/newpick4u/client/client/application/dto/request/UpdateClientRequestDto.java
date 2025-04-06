package com.newpick4u.client.client.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateClientRequestDto(
    @NotNull @Size(min = 4, max = 50) String name,
    @NotNull @Email String email,
    @NotNull @Size(min = 10, max = 50) String phone,
    @NotNull @Size(min = 10, max = 50) String address
) {

}
