package com.closememo.command.domain;

import org.springframework.http.HttpStatus;

public class IllegalResourceException extends BusinessException {

  private static final long serialVersionUID = 2331247122202026255L;

  public IllegalResourceException() {
  }

  public IllegalResourceException(String message) {
    super(message);
  }

  public IllegalResourceException(String message, Throwable cause) {
    super(message, cause);
  }

  public IllegalResourceException(Throwable cause) {
    super(cause);
  }

  public IllegalResourceException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  @Override
  public HttpStatus getHttpStatus() {
    return HttpStatus.BAD_REQUEST;
  }

  @Override
  public boolean isNecessaryToLog() {
    return false;
  }
}
