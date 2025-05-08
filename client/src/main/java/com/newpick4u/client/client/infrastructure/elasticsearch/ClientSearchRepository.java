package com.newpick4u.client.client.infrastructure.elasticsearch;

import com.newpick4u.client.client.domain.entity.ClientDocument;
import java.util.UUID;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ClientSearchRepository extends ElasticsearchRepository<ClientDocument, UUID> {

}
