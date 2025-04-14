package com.newpick4u.client.advertisement.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;

import com.newpick4u.client.advertisement.application.client.NewsClient;
import com.newpick4u.client.advertisement.application.message.producer.PointUpdateProducer;
import com.newpick4u.client.advertisement.application.message.request.PointRequestMessage;
import com.newpick4u.client.advertisement.domain.entity.Advertisement;
import com.newpick4u.client.advertisement.domain.entity.Advertisement.AdvertisementType;
import com.newpick4u.client.advertisement.domain.repository.AdvertisementRepository;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class PointGrantedCountUpdateTest {

  @Autowired
  private AdvertisementService advertisementService;
  @Autowired
  private AdvertisementRepository advertisementRepository;
  @Autowired
  private PointUpdateProducer pointUpdateProducer;
  @Autowired
  private NewsClient newsClient;

  @Test
  @DisplayName("분산락을 이용한 포인트 지급 횟수 갱신 테스트")
  void distributedLockTest() throws InterruptedException {

    // given
    Advertisement advertisement = Advertisement.create(UUID.randomUUID(), UUID.randomUUID(), "예시",
        "내용", AdvertisementType.BANNER, "www.com", 500000L, 50);
    int threadCount = 50;
    ExecutorService executorService = Executors.newFixedThreadPool(25);
    CountDownLatch latch = new CountDownLatch(threadCount);
    Advertisement savedAdvertisement = advertisementRepository.save(advertisement);

    // when
    for (int i = 0; i < threadCount; i++) {
      executorService.submit(() -> {
        PointRequestMessage message = new PointRequestMessage(1L,
            savedAdvertisement.getAdvertisementId(), 500);
        advertisementService.updatePointGrantedCount(message);
        latch.countDown();
      });
    }

    latch.await();

    // then
    Advertisement updatedAdvertisement = advertisementRepository.findById(
            savedAdvertisement.getAdvertisementId())
        .get();

    assertThat(updatedAdvertisement.getPointGrantCount()).isEqualTo(50);
    assertThat(updatedAdvertisement.isPointGrantFinished()).isTrue();

  }
}
