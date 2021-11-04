package com.closememo.command.infra.http.mail;

import java.time.Duration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class MailClient {

  public static final String X_SYSTEM_KEY_HEADER = "X-SYSTEM-KEY";

  private final RestTemplate restTemplate;

  public MailClient(RestTemplateBuilder restTemplateBuilder,
      MailProperties properties) {

    this.restTemplate = restTemplateBuilder
        .rootUri(properties.getRootUri())
        .defaultHeader(X_SYSTEM_KEY_HEADER, properties.getToken())
        .setReadTimeout(Duration.ofMillis(properties.getReadTimeout()))
        .build();
  }

  public void sendMail(SendMailRequest request) {
    RequestEntity<SendMailRequest> requestEntity = RequestEntity
        .post("/mail-sender/system/send-mail")
        .accept(MediaType.APPLICATION_JSON)
        .body(request);

    ResponseEntity<Void> response = restTemplate.exchange(requestEntity, Void.class);

    validateResponse(response, "[MAIL] sendMail failed.");
  }

  private void validateResponse(@NonNull ResponseEntity<?> response, String errorMessage) {
    if (response.getStatusCode().is5xxServerError()) {
      throw new MailInternalServerException(errorMessage);
    }

    if (response.getStatusCode().isError()) {
      throw new MailClientException(errorMessage);
    }
  }
}
