package com.newpick4u.client.client.application.dto.response;

import com.newpick4u.client.client.domain.entity.Client.Industry;
import java.time.LocalDateTime;

public record GetClientResponseDto(String name, Industry industry, String email,
                                   LocalDateTime createdAt, Long createdBy,
                                   LocalDateTime updatedAt, Long updatedBy) {

}
