package com.closememo.command.infra.http.naver;

import com.closememo.command.domain.InternalServerException;

public class NaverOAuthClientException extends InternalServerException {

  private static final long serialVersionUID = 5236320717186877241L;

  public NaverOAuthClientException() {
  }

  public NaverOAuthClientException(String message) {
    super(message);
  }
}
