package com.closememo.command.config.http;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties("http.rest-template.configs.default")
public class RestTemplateProperty {

  private int readTimeout;
  private int connectionTimeout;
  private int maxConnectionCount;
  private int maxConnectionPerRoute;
}
