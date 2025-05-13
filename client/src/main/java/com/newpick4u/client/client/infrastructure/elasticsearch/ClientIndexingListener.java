package com.newpick4u.client.client.infrastructure.elasticsearch;

import com.newpick4u.client.client.application.event.ClientSavedEvent;
import com.newpick4u.client.client.domain.entity.ClientDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Component
public class ClientIndexingListener {

  private final ClientSearchRepository searchRepository;

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void save(ClientSavedEvent event) {

    ClientDocument clientDocument = ClientDocument.from(event.id(), event.name(), event.address(),
        event.email(), event.phone(), event.industry());
    searchRepository.save(clientDocument);
  }


}
