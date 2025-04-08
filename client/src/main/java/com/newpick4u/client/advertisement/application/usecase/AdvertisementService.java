package com.newpick4u.client.advertisement.application.usecase;

import com.newpick4u.client.advertisement.application.dto.request.CreateAdvertiseRequestDto;
import java.util.UUID;

public interface AdvertisementService {

  public UUID createAdvertisement(CreateAdvertiseRequestDto request);

}
