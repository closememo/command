package com.closememo.command.infra.http.naver;

import feign.codec.ErrorDecoder;
import feign.codec.ErrorDecoder.Default;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(value = "naver-api-client", configuration = NaverApiClient.NaverApiClientConfig.class)
public interface NaverApiClient {

  @GetMapping("/v1/nid/me")
  NaverProfileResponse getNaverProfile(@RequestHeader("Authorization") String authorization);

  class NaverApiClientConfig {

    @Bean
    public ErrorDecoder errorDecoder() {
      return (methodKey, response) -> {
        String url = response.request().url();
        HttpStatusCode status = HttpStatusCode.valueOf(response.status());
        if (status.is5xxServerError()) {
          return new NaverApiInternalServerException("url=" + url);
        }
        if (status.isError()) {
          return new NaverApiClientException("url=" + url);
        }
        return new Default().decode(methodKey, response);
      };
    }
  }
}
