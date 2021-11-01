package com.closememo.command.config.http;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.ResponseErrorHandler;

@Slf4j
public class DefaultResponseErrorHandler implements ResponseErrorHandler {

  @Override
  public boolean hasError(ClientHttpResponse response) throws IOException {
    HttpStatus statusCode = HttpStatus.resolve(response.getRawStatusCode());
    return (statusCode == null || statusCode.isError());
  }

  @Override
  public void handleError(ClientHttpResponse response) throws IOException {
    // Request URL 을 알 수 없는 경우에는 아무런 작업을 수행하지 않는다.
  }

  @Override
  public void handleError(URI url, HttpMethod method, ClientHttpResponse response)
      throws IOException {
    // Request URL 을 알고 있는 경우에는 Error 레벨로 로그를 기록한다.
    String body = StreamUtils.copyToString(response.getBody(), Charset.defaultCharset());
    log.error(String.format("[FAIL] URL:%s, HTTP Method:%s, HTTP Status:%s(%d), body:%s",
        url.toString(), method.name(), response.getStatusText(), response.getRawStatusCode(), body));
  }
}
