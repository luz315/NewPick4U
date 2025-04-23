package com.newpick4u.client.advertisement.application.usecase;

import com.newpick4u.client.advertisement.application.client.NewsClient;
import com.newpick4u.client.advertisement.application.dto.request.CreateAdvertiseRequestDto;
import com.newpick4u.client.advertisement.application.dto.response.GetNewsResponseDto;
import com.newpick4u.client.advertisement.application.exception.AdvertisementException;
import com.newpick4u.client.advertisement.application.exception.AdvertisementException.NotFoundException;
import com.newpick4u.client.advertisement.application.message.request.PointRequestFailureMessage;
import com.newpick4u.client.advertisement.application.message.request.PointRequestMessage;
import com.newpick4u.client.advertisement.application.message.request.PointUpdateMessage;
import com.newpick4u.client.advertisement.domain.entity.Advertisement;
import com.newpick4u.client.advertisement.domain.repository.AdvertisementRepository;
import com.newpick4u.client.global.aop.DistributedLock;
import com.newpick4u.client.global.exception.DomainExceptionFactory;
import com.newpick4u.common.response.ApiResponse;
import io.micrometer.core.instrument.MeterRegistry;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdvertisementServiceImpl implements AdvertisementService {

  private final AdvertisementRepository advertisementRepository;
  private final NewsClient newsClient;
  private final MeterRegistry meterRegistry;
  private final ApplicationEventPublisher eventPublisher;


  @Transactional
  public UUID createAdvertisement(CreateAdvertiseRequestDto request) {
    // TODO 뉴스 도메인의 객체 반환방식에 따라 변돋이 생길 수 있음
    UUID newsId = getNews(request);
    if (validateCondition(request.title(), request.url())) {
      throw new AdvertisementException.AlreadyExistsTitleOrUrlException();
    }
    Advertisement advertisement = Advertisement.create(request.clientId(), request.newsId(),
        request.title(), request.content(),
        request.type(), request.url(), request.budget(), request.maxPointGrantCount(),
        request.point());
    Advertisement saveAdvertisement = advertisementRepository.save(advertisement);
    return saveAdvertisement.getAdvertisementId();
  }

  // ToDo : 고도화 파트에서 파티션 정책 수정으로 인해 변경될 가능성 존재
  @DistributedLock(key = "'advertise:' + #message.advertisementId")
  @Override
  public void updatePointGrantedCount(PointRequestMessage message) {
    final String pointRequestMetric = "point_request_processed_total";
    Advertisement advertisement = advertisementRepository.findById(message.advertisementId())
        .orElseThrow(() -> DomainExceptionFactory.getDomainException(NotFoundException.class));
    if (advertisement.isPointGrantFinished()) {
      return;
    }
    increasePointGrantedCount(advertisement);
    Advertisement updatedAdvertisement = advertisementRepository.save(advertisement);
    // ✅ TPS 측정을 위한 카운터 증가
    meterRegistry.counter(pointRequestMetric).increment();
    eventPublisher.publishEvent(
        PointUpdateMessage.of(message.userId(), updatedAdvertisement.getPoint(),
            updatedAdvertisement.getAdvertisementId()));
  }

  @DistributedLock(key = "'advertise:' + #message.advertisementId")
  @Override
  public void cancelPointRequest(PointRequestFailureMessage message) {
    Advertisement advertisement = advertisementRepository.findById(message.advertisementId())
        .orElseThrow(() -> DomainExceptionFactory.getDomainException(NotFoundException.class));
    if (advertisement.isPointGrantFinished()) {
      advertisement.reopenPointGrant();
    }
    reducePointGrantedCount(advertisement);
    advertisementRepository.save(advertisement);
  }

  private void increasePointGrantedCount(Advertisement advertisement) {
    advertisement.incrementPointGrantCount();
    if (advertisement.isMaxPointGrantCountEqualToCurrentPointGrantCount()) {
      advertisement.updateIsPointGrantFinished();
    }
  }

  private void reducePointGrantedCount(Advertisement advertisement) {
    final int minimumValue = 0;
    if (advertisement.getPointGrantCount() > minimumValue) {
      advertisement.reducePointGrantCount();
    }
  }

  private UUID getNews(CreateAdvertiseRequestDto requestDto) {
    ResponseEntity<ApiResponse<GetNewsResponseDto>> response = newsClient.getNews(
        requestDto.newsId());
    if (Objects.nonNull(response) && response.getStatusCode().is2xxSuccessful()) {
      UUID newsId = Objects.requireNonNull(response.getBody()).data().getId();
      return newsId;
    }
    throw new AdvertisementException.NewsNotFoundException();
  }

  private boolean validateCondition(String title, String url) {
    return advertisementRepository.existsByTitleOrUrl(title, url);
  }

}
