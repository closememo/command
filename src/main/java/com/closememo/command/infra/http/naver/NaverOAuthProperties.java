package com.closememo.command.infra.http.naver;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@ConfigurationProperties("http.naver.auth")
@Configuration
public class NaverOAuthProperties {

  private String rootUri;
  private String clientId;
  private String clientSecret;
}
