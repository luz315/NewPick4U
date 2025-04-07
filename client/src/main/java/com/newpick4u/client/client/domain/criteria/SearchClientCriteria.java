package com.newpick4u.client.client.domain.criteria;

import static com.newpick4u.client.client.domain.entity.QClient.client;

import com.newpick4u.client.client.domain.entity.Client.Industry;
import com.querydsl.core.BooleanBuilder;
import java.util.Objects;
import lombok.Builder;
import org.springframework.util.StringUtils;

@Builder
public record SearchClientCriteria(String name, Industry industry, String email,
                                   String phone, String address) {

  public BooleanBuilder booleanBuilder() {
    BooleanBuilder builder = new BooleanBuilder();

    addNameCondition(builder);
    addIndustryCondition(builder);
    addEmailCondition(builder);
    addPhoneCondition(builder);
    addAddressCondition(builder);

    return builder;
  }

  private void addNameCondition(BooleanBuilder builder) {
    if (StringUtils.hasText(name)) {
      builder.and(client.name.contains(name));
    }
  }

  private void addIndustryCondition(BooleanBuilder builder) {
    if (Objects.nonNull(industry)) {
      builder.and(client.industry.eq(industry));
    }
  }

  private void addEmailCondition(BooleanBuilder builder) {
    if (StringUtils.hasText(email)) {
      builder.and(client.email.contains(email));
    }
  }

  private void addPhoneCondition(BooleanBuilder builder) {
    if (StringUtils.hasText(phone)) {
      builder.and(client.phone.contains(phone));
    }
  }

  private void addAddressCondition(BooleanBuilder builder) {
    if (StringUtils.hasText(address)) {
      builder.and(client.address.contains(address));
    }
  }

}
