package com.closememo.command.infra.http.naver;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@ConfigurationProperties("http.naver.api")
@Configuration
public class NaverApiProperties {

  private String rootUri;
}
