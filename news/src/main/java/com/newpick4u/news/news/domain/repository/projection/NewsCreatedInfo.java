package com.newpick4u.news.news.domain.repository.projection;

import java.time.LocalDateTime;
import java.util.UUID;

public interface NewsCreatedInfo {
    UUID getId();
    LocalDateTime getCreatedAt();
}
