package com.newpick4u.client.advertisement.domain.repository;

import com.newpick4u.client.advertisement.domain.entity.Advertisement;

public interface AdvertisementRepository {

  Advertisement save(Advertisement advertisement);

  boolean existsByTitleOrUrl(String title, String url);
}
