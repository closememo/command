package com.closememo.command.config.http;

import io.micrometer.core.instrument.util.IOUtils;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StopWatch;

@Slf4j
public class RequestResponseLoggingInterceptor implements ClientHttpRequestInterceptor {

  private static final String MULTIPART_BODY = "[multipart body]";

  @Override
  public ClientHttpResponse intercept(HttpRequest request, byte[] body,
      ClientHttpRequestExecution execution) throws IOException {
    log.debug("\n===========================request begin==========================="
            + "\nURI : {}"
            + "\nMethod : {}"
            + "\nRequest Headers : {}"
            + "\nRequest Body : {}"
            + "\n============================request end============================",
        request.getURI(),
        request.getMethod(),
        request.getHeaders(),
        isMultipart(request) ? MULTIPART_BODY : new String(body, StandardCharsets.UTF_8));

    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    ClientHttpResponse response = execution.execute(request, body);
    stopWatch.stop();

    log.debug("\n==========================response begin==========================="
            + "\nURI : {}"
            + "\nResponse Status : {}"
            + "\nResponse Headers: {}"
            + "\nResponse Body : {}"
            + "\nTaken Time : {} ms"
            + "\n===========================response end============================",
        request.getURI(),
        response.getStatusCode(),
        response.getHeaders(),
        IOUtils.toString(response.getBody(), StandardCharsets.UTF_8),
        stopWatch.getLastTaskTimeMillis());

    return response;
  }

  private static boolean isMultipart(HttpRequest request) {
    return MediaType.MULTIPART_FORM_DATA.includes(request.getHeaders().getContentType());
  }
}
