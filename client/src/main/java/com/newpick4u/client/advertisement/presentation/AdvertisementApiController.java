package com.newpick4u.client.advertisement.presentation;

import com.newpick4u.client.advertisement.application.dto.request.CreateAdvertiseRequestDto;
import com.newpick4u.client.advertisement.application.usecase.AdvertisementService;
import com.newpick4u.common.response.ApiResponse;
import jakarta.validation.Valid;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/advertisements")
@RequiredArgsConstructor
public class AdvertisementApiController {

  private final AdvertisementService advertisementService;

  @PostMapping
  public ResponseEntity<ApiResponse<Map<String, UUID>>> createAdvertisement(@RequestBody @Valid
  CreateAdvertiseRequestDto request) {
    UUID savedAdvertisementId = advertisementService.createAdvertisement(request);
    return ResponseEntity.status(HttpStatus.CREATED.value())
        .body(ApiResponse.of(HttpStatus.CREATED,
            Map.of("savedAdvertisementId", savedAdvertisementId)));
  }
}
