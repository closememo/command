package com.closememo.command.infra.http.naver;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class NaverOAuthClient {

  private final RestTemplate restTemplate;
  private final NaverOAuthProperties properties;

  public NaverOAuthClient(RestTemplateBuilder restTemplateBuilder,
      NaverOAuthProperties properties) {
    this.restTemplate = restTemplateBuilder
        .rootUri(properties.getRootUri())
        .build();
    this.properties = properties;
  }

  public NaverTokenResponse getAccessToken(String code, String state) {
    String uri = UriComponentsBuilder.fromUriString("/oauth2.0/token")
        .queryParam("grant_type", "authorization_code")
        .queryParam("client_id", properties.getClientId())
        .queryParam("client_secret", properties.getClientSecret())
        .queryParam("code", code)
        .queryParam("state", state)
        .build()
        .toUriString();

    ResponseEntity<NaverTokenResponse> response = restTemplate.getForEntity(
        uri, NaverTokenResponse.class);

    validateResponse(response, "[NAVER OAuth] getAccessToken failed.");
    return response.getBody();
  }

  private void validateResponse(@NonNull ResponseEntity<?> response, String errorMessage) {
    if (response.getStatusCode().is5xxServerError()) {
      throw new NaverOAuthInternalServerException(errorMessage);
    }

    if (response.getStatusCode().isError()) {
      throw new NaverOAuthClientException(errorMessage);
    }
  }
}
