package com.newpick4u.client.advertisement.application.usecase;

import com.newpick4u.client.advertisement.application.dto.request.CreateAdvertiseRequestDto;
import com.newpick4u.client.advertisement.application.message.request.PointRequestFailureMessage;
import com.newpick4u.client.advertisement.application.message.request.PointRequestMessage;
import com.newpick4u.common.resolver.dto.CurrentUserInfoDto;
import java.util.UUID;

public interface AdvertisementService {

  public UUID createAdvertisement(CreateAdvertiseRequestDto request, CurrentUserInfoDto userInfo);

  public void updatePointGrantedCount(PointRequestMessage message);

  public void cancelPointRequest(PointRequestFailureMessage message);


}
