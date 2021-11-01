package com.closememo.command.infra.http.naver;

import com.closememo.command.domain.InternalServerException;

public class NaverApiClientException extends InternalServerException {

  private static final long serialVersionUID = 7698713890598994560L;

  public NaverApiClientException() {
  }

  public NaverApiClientException(String message) {
    super(message);
  }
}
