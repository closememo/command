package com.closememo.command.infra.http.naver;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class NaverApiClient {

  private final RestTemplate restTemplate;

  public NaverApiClient(RestTemplateBuilder restTemplateBuilder,
      NaverApiProperties naverApiProperties) {
    this.restTemplate = restTemplateBuilder
        .rootUri(naverApiProperties.getRootUri())
        .build();
  }

  public NaverProfileResponse getNaverProfile(String tokenType, String accessToken) {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", tokenType + " " + accessToken);

    RequestEntity<Void> requestEntity = RequestEntity
        .get("/v1/nid/me")
        .headers(headers)
        .build();

    ResponseEntity<NaverProfileResponse> response = restTemplate.exchange(
        requestEntity, NaverProfileResponse.class);

    validateResponse(response, "[NAVER OpenAPI] getNaverProfile failed.");
    return response.getBody();
  }

  private void validateResponse(@NonNull ResponseEntity<?> response, String errorMessage) {
    if (response.getStatusCode().is5xxServerError()) {
      throw new NaverApiInternalServerException(errorMessage);
    }

    if (response.getStatusCode().isError()) {
      throw new NaverApiClientException(errorMessage);
    }
  }
}
