package com.newpick4u.client.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;

@Configuration
public class ElasticConfig extends ElasticsearchConfiguration {

  @Value("${spring.data.elasticsearch.host}")
  private String hostUrl;
  @Value("${spring.data.elasticsearch.username}")
  private String username;
  @Value("${spring.data.elasticsearch.password}")
  private String password;

  @Override
  public ClientConfiguration clientConfiguration() {
    return ClientConfiguration.builder()
        .connectedTo(hostUrl)
        .withBasicAuth(username, password)
        .build();
  }
}
