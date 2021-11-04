package com.closememo.command.infra.http.mail;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@ConfigurationProperties("http.mail")
@Configuration
public class MailProperties {

  private String rootUri;
  private String token;
  private int readTimeout;
}
