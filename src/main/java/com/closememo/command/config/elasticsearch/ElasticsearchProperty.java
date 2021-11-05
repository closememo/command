package com.closememo.command.config.elasticsearch;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties("elasticsearch.configs.default")
public class ElasticsearchProperty {

  private String hostName;
  private int port;
  private int connectionTimeout;
  private int socketTimeout;
}
