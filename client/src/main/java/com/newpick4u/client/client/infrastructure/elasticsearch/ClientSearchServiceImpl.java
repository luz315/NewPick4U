package com.newpick4u.client.client.infrastructure.elasticsearch;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import com.newpick4u.client.client.application.dto.request.SearchClientRequestDto;
import com.newpick4u.client.client.application.usecase.ClientSearchService;
import com.newpick4u.client.client.domain.entity.ClientDocument;
import com.newpick4u.common.response.PageResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class ClientSearchServiceImpl implements ClientSearchService {

  private final ElasticsearchOperations elasticsearchOperations;

  @Override
  public PageResponse<ClientDocument> search(SearchClientRequestDto request, Pageable pageable) {
    NativeQueryBuilder queryBuilder = NativeQuery.builder();
    queryBuilder.withTrackScores(false);
    BoolQuery.Builder boolQuery = QueryBuilders.bool();

    if (request.name() != null && !request.name().isBlank()) {
      Query nameQuery = QueryBuilders.match(m -> m.field("name").query(request.name()));
      boolQuery.must(nameQuery);
    }

    if (request.email() != null && !request.email().isBlank()) {
      Query emailQuery = QueryBuilders.match(m -> m.field("email").query(request.email()));
      boolQuery.must(emailQuery);
    }

    if (request.address() != null && !request.address().isBlank()) {
      Query addressQuery = QueryBuilders.match(m -> m.field("address").query(request.address()));
      boolQuery.must(addressQuery);
    }

    if (request.phone() != null && !request.phone().isBlank()) {
      Query phoneQuery = QueryBuilders.term(t -> t.field("phone").value(request.phone()));
      boolQuery.must(phoneQuery);
    }

    if (request.industry() != null && !request.industry().isBlank()) {
      Query industryQuery = QueryBuilders.term(t -> t.field("industry").value(request.industry()));
      boolQuery.must(industryQuery);
    }

    queryBuilder.withQuery(boolQuery.build()._toQuery());
    queryBuilder.withPageable(pageable);
    // 이름 정렬 (오름차순)
    Pageable sortedPageable = PageRequest.of(
        pageable.getPageNumber(),
        pageable.getPageSize(),
        Sort.by(Sort.Order.asc("industry")) // 또는 "name"
    );
    queryBuilder.withPageable(sortedPageable);

    NativeQuery query = queryBuilder.build();

    SearchHits<ClientDocument> hits = elasticsearchOperations.search(query, ClientDocument.class);
    List<ClientDocument> content = hits.stream().map(SearchHit::getContent).toList();
    Page<ClientDocument> page = new PageImpl<>(content, sortedPageable, hits.getTotalHits());

    return PageResponse.from(page);
  }
}
