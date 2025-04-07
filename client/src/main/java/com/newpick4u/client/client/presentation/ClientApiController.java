package com.newpick4u.client.client.presentation;

import com.newpick4u.client.client.application.dto.request.CreateClientRequestDto;
import com.newpick4u.client.client.application.dto.request.UpdateClientRequestDto;
import com.newpick4u.client.client.application.dto.response.GetClientResponseDto;
import com.newpick4u.client.client.application.usecase.ClientService;
import com.newpick4u.client.client.domain.criteria.SearchClientCriteria;
import com.newpick4u.common.resolver.annotation.CurrentUserInfo;
import com.newpick4u.common.resolver.dto.CurrentUserInfoDto;
import com.newpick4u.common.response.ApiResponse;
import com.newpick4u.common.response.PageResponse;
import jakarta.validation.Valid;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/clients")
@RequiredArgsConstructor
public class ClientApiController {

  private final ClientService clientService;

  @PostMapping
  public ResponseEntity<ApiResponse<Map<String, UUID>>> saveClient(
      @RequestBody @Valid CreateClientRequestDto request) {
    UUID savedClientId = clientService.saveClient(request);

    return ResponseEntity.status(HttpStatus.CREATED.value())
        .body(ApiResponse.of(HttpStatus.CREATED, Map.of("savedClientId", savedClientId)));
  }

  @GetMapping("/{clientID}")
  public ResponseEntity<ApiResponse<GetClientResponseDto>> getClient(
      @PathVariable("clientID") UUID clientId) {
    GetClientResponseDto responseDto = clientService.getClient(clientId);
    return ResponseEntity.status(HttpStatus.OK.value())
        .body(ApiResponse.of(HttpStatus.OK, responseDto));
  }

  @GetMapping("/search")
  public ResponseEntity<ApiResponse<PageResponse<GetClientResponseDto>>> getClients(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @ModelAttribute SearchClientCriteria criteria) {

    Pageable pageable = PageRequest.of(page, size);
    Page<GetClientResponseDto> responseDtos = clientService.getClients(pageable, criteria);
    return ResponseEntity.status(HttpStatus.OK.value())
        .body(ApiResponse.of(HttpStatus.OK, PageResponse.from(responseDtos)));
  }


  @PatchMapping("/{clientId}")
  public ResponseEntity<ApiResponse<Map<String, UUID>>> updateClient(
      @PathVariable("clientId") UUID clientId,
      @RequestBody @Valid UpdateClientRequestDto request) {

    UUID updatedClientId = clientService.updateClient(clientId, request);
    return ResponseEntity.status(HttpStatus.OK.value())
        .body(ApiResponse.of(HttpStatus.OK, Map.of("updatedClientId", updatedClientId)));
  }

  @DeleteMapping("/{clientId}")
  public ResponseEntity<ApiResponse<Map<String, UUID>>> deleteClient(
      @PathVariable("clientId") UUID clientId, @CurrentUserInfo
      CurrentUserInfoDto userInfoDto) {
    UUID deletedClientId = clientService.deleteClient(clientId, userInfoDto.userId());
    return ResponseEntity.status(HttpStatus.OK.value())
        .body(ApiResponse.of(HttpStatus.OK, Map.of("deletedClientId", deletedClientId)));
  }

}
