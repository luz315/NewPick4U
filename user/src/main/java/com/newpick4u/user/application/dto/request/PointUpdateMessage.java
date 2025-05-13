package com.newpick4u.user.application.dto.request;

import java.util.UUID;

public record PointUpdateMessage(Long userId, Integer point, UUID advertisementId) {

}
