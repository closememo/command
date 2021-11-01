package com.closememo.command.config.http;

import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.lang.NonNull;
import org.springframework.web.client.ResponseErrorHandler;

@Slf4j
@Configuration
public class RestTemplateConfig {

  @Bean
  public ResponseErrorHandler responseErrorHandler() {
    return new DefaultResponseErrorHandler();
  }

  @Bean
  public RestTemplateBuilder restTemplateBuilder(RestTemplateProperty defaultRestTemplateProperty,
      HttpMessageConverters messageConverters) {

    RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder()
        .messageConverters(messageConverters.getConverters())
        .setReadTimeout(Duration.ofMillis(defaultRestTemplateProperty.getReadTimeout()))
        .setConnectTimeout(Duration.ofMillis(defaultRestTemplateProperty.getConnectionTimeout()))
        .errorHandler(responseErrorHandler())
        .requestFactory(() -> new BufferingClientHttpRequestFactory(
            new HttpComponentsClientHttpRequestFactory(
                HttpClientBuilder.create()
                    .setSSLHostnameVerifier(new NoopHostnameVerifier())
                    .setMaxConnPerRoute(defaultRestTemplateProperty.getMaxConnectionPerRoute())
                    .setMaxConnTotal(defaultRestTemplateProperty.getMaxConnectionCount())
                    .build())));

    return adjustLoggingInterceptor(restTemplateBuilder);
  }

  private RestTemplateBuilder adjustLoggingInterceptor(
      @NonNull RestTemplateBuilder restTemplateBuilder) {
    // Debug Level 이 활성화된 경우가 아니면, Interceptor 적용을 생략한다.
    if (!log.isDebugEnabled()) {
      return restTemplateBuilder;
    }

    // 인터셉터를 적용한 새로운 RestTemplateBuilder 를 만들어 반환한다.
    return restTemplateBuilder.additionalInterceptors(new RequestResponseLoggingInterceptor());
  }
}
