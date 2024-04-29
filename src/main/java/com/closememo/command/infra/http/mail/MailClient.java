package com.closememo.command.infra.http.mail;

import feign.codec.ErrorDecoder;
import feign.codec.ErrorDecoder.Default;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "mail-client", configuration = MailClient.MailClientConfig.class)
public interface MailClient {

  @GetMapping("/mail-sender/system/send-mail")
  void sendMail(@RequestBody SendMailRequest request);

  class MailClientConfig {

    @Bean
    public ErrorDecoder errorDecoder() {
      return (methodKey, response) -> {
        String url = response.request().url();
        HttpStatusCode status = HttpStatusCode.valueOf(response.status());
        if (status.is5xxServerError()) {
          return new MailInternalServerException("url=" + url);
        }
        if (status.isError()) {
          return new MailClientException("url=" + url);
        }
        return new Default().decode(methodKey, response);
      };
    }
  }
}
