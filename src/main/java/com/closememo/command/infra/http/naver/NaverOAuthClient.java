package com.closememo.command.infra.http.naver;

import feign.codec.ErrorDecoder;
import feign.codec.ErrorDecoder.Default;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "naver-oauth-client", configuration = NaverOAuthClient.NaverOauthClientConfig.class)
public interface NaverOAuthClient {

  @GetMapping("/oauth2.0/token")
  NaverTokenResponse getAccessToken(@RequestParam("code") String code, @RequestParam String state);

  class NaverOauthClientConfig {

    @Bean
    public ErrorDecoder errorDecoder() {
      return (methodKey, response) -> {
        String url = response.request().url();
        HttpStatusCode status = HttpStatusCode.valueOf(response.status());
        if (status.is5xxServerError()) {
          return new NaverOAuthInternalServerException("url=" + url);
        }
        if (status.isError()) {
          return new NaverOAuthClientException("url=" + url);
        }
        return new Default().decode(methodKey, response);
      };
    }
  }
}
