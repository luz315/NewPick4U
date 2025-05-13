package com.newpick4u.client.client.infrastructure.jpa;

import static com.newpick4u.client.client.domain.entity.QClient.client;

import com.newpick4u.client.client.application.dto.response.GetClientResponseDto;
import com.newpick4u.client.client.domain.criteria.SearchClientCriteria;
import com.newpick4u.client.client.domain.entity.Client;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ClientRepositoryCustomImpl implements ClientRepositoryCustom {

  private final JPAQueryFactory queryFactory;


  @Override
  public Page<GetClientResponseDto> getClients(Pageable pageable,
      SearchClientCriteria criteria) {
    final int minimumCount = 0;

    BooleanBuilder booleanBuilder = criteria.booleanBuilder();

    List<Client> clients = queryFactory.selectFrom(client)
        .from(client)
        .where(booleanBuilder)
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .orderBy(client.industry.asc())
        .fetch();

    int total = clients.size();

    List<GetClientResponseDto> responseDtos = clients.stream()
        .map(client -> new GetClientResponseDto(client.getName(), client.getIndustry(),
            client.getAddress(),
            client.getPhone(), client.getEmail(), client.getCreatedAt(), client.getCreatedBy(),
            client.getUpdatedAt(),
            client.getUpdatedBy()))
        .toList();

    return new PageImpl<>(responseDtos, pageable, total);

  }
}
