package com.newpick4u.client.client.application.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.newpick4u.client.client.application.dto.request.CreateClientRequestDto;
import com.newpick4u.client.client.application.dto.request.UpdateClientRequestDto;
import com.newpick4u.client.client.application.exception.ClientException;
import com.newpick4u.client.client.domain.entity.Client;
import com.newpick4u.client.client.domain.entity.Client.Industry;
import com.newpick4u.client.client.domain.repository.ClientRepository;
import java.util.Optional;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class ClientServiceImplTest {

  @InjectMocks
  private ClientServiceImpl clientService;
  @Mock
  private ClientRepository clientRepository;

  @Test
  @DisplayName("고객사 생성 테스트 - 성공 케이스")
  void createClient() {

    // given
    CreateClientRequestDto request = new CreateClientRequestDto(
        "스파르타배송", Industry.TRANSPORTATION, "sparta@ex.com",
        "010-1111-2222", "스파르타 코딩클럽");

    Client client = Client.create(request.name(), request.industry(), request.email(),
        request.phone(),
        request.address());

    ReflectionTestUtils.setField(client, "clientId", UUID.randomUUID());

    when(clientRepository.existsByEmail(request.email())).thenReturn(false);
    when(clientRepository.existsByPhone(request.phone())).thenReturn(false);
    when(clientRepository.save(any(Client.class))).thenReturn(client);

    // when
    UUID saveClientId = clientService.saveClient(request);

    // then
    assertNotNull(saveClientId);
    assertEquals(client.getClientId(), saveClientId);

  }

  @Test
  @DisplayName("고객사 생성 테스트 - 전화번호 중복 케이스")
  void createClient_withDuplicatePhone() {
    // given
    CreateClientRequestDto request = new CreateClientRequestDto(
        "스파르타배송", Industry.TRANSPORTATION, "sparta@ex.com",
        "010-1111-2222", "스파르타 코딩클럽");

    when(clientRepository.existsByEmail(request.email())).thenReturn(false);
    when(clientRepository.existsByPhone(request.phone())).thenReturn(true);

    // when & then
    Assertions.assertThatThrownBy(() -> clientService.saveClient(request))
        .isInstanceOf(ClientException.DuplicatePhoneNumberException.class);
  }

  @Test
  @DisplayName("고객사 생성 테스트 - 이메일 중복 케이스")
  void createClient_withDuplicateEmail() {
    // given
    CreateClientRequestDto request = new CreateClientRequestDto(
        "스파르타배송", Industry.TRANSPORTATION, "sparta@ex.com",
        "010-1111-2222", "스파르타 코딩클럽");

    when(clientRepository.existsByEmail(request.email())).thenReturn(true);

    // when & then
    Assertions.assertThatThrownBy(() -> clientService.saveClient(request))
        .isInstanceOf(ClientException.DuplicateEmailException.class);
  }

  @Test
  @DisplayName("고객사 정보 수정 테스트 - 성공 케이스")
  void updateClient() {
    // given
    UpdateClientRequestDto request = new UpdateClientRequestDto("페르시아 운송",
        "changed@ex.com", "010-3333-4444", "페르시아 코딩클럽");

    Client original = Client.create("스파르타 운송", Industry.TRANSPORTATION, "sparta@ex.com",
        "010-1111-2222", "스파르타 코딩클럽");

    Client updatedClient = Client.create(request.name(), Industry.TRANSPORTATION, request.email(),
        request.phone(), request.address());

    UUID clientId = UUID.randomUUID();

    ReflectionTestUtils.setField(original, "clientId", clientId);
    ReflectionTestUtils.setField(updatedClient, "clientId", clientId);

    when(clientRepository.findById(clientId)).thenReturn(Optional.of(original));
    when(clientRepository.save(any(Client.class))).thenReturn(updatedClient);

    // when
    UUID updatedClientId = clientService.updateClient(clientId, request);

    // then
    assertNotNull(updatedClientId);
    assertEquals(updatedClient.getClientId(), original.getClientId());

  }

  @Test
  @DisplayName("고객사 정보 수정 테스트 - 존재하지 않는 고객사에 대한 수정 요청")
  void updateClient_withNotExistingClient() {
    // given
    UpdateClientRequestDto request = new UpdateClientRequestDto("페르시아 운송",
        "changed@ex.com", "010-3333-4444", "페르시아 코딩클럽");

    UUID notExistsClientId = UUID.randomUUID();

    when(clientRepository.findById(notExistsClientId)).
        thenThrow(ClientException.NotFoundException.class);

    // when & then
    Assertions.assertThatThrownBy(() -> clientService.updateClient(notExistsClientId, request))
        .isInstanceOf(ClientException.NotFoundException.class);
  }

  @Test
  @DisplayName("고객사 정보 삭제 테스트 - 성공 케이스")
  void deleteClient() {
    // given
    UUID toBeDeletedClientId = UUID.randomUUID();

    Client original = Client.create("스파르타 운송", Industry.TRANSPORTATION, "sparta@ex.com",
        "010-1111-2222", "스파르타 코딩클럽");
    Client deletedClient = Client.create("스파르타 운송", Industry.TRANSPORTATION, "sparta@ex.com",
        "010-1111-2222", "스파르타 코딩클럽");

    ReflectionTestUtils.setField(original, "clientId", toBeDeletedClientId);
    ReflectionTestUtils.setField(deletedClient, "clientId", toBeDeletedClientId);

    when(clientRepository.findById(toBeDeletedClientId)).thenReturn(Optional.of(original));
    when(clientRepository.save(any(Client.class))).thenReturn(deletedClient);

    // when
    UUID deletedClientId = clientService.deleteClient(toBeDeletedClientId, 1L);

    // then
    assertNotNull(deletedClientId);
    assertEquals(toBeDeletedClientId, original.getClientId());
  }

  @Test
  @DisplayName("고객사 정보 삭제 테스트 - 존재하지 않는 고객사")
  void deleteClient_withNotExistsClient() {
    // given
    UUID notExistsClientId = UUID.randomUUID();

    Long deletedBy = 1L;

    when(clientRepository.findById(notExistsClientId)).
        thenThrow(ClientException.NotFoundException.class);

    // when & then
    Assertions.assertThatThrownBy(() -> clientService.deleteClient(notExistsClientId, deletedBy))
        .isInstanceOf(ClientException.NotFoundException.class);
  }
}