package com.closememo.command.domain;

import org.springframework.http.HttpStatus;

public class ResourceAlreadyExistException extends BusinessException {

  private static final long serialVersionUID = 116242001526209673L;

  public ResourceAlreadyExistException() {
  }

  public ResourceAlreadyExistException(String message) {
    super(message);
  }

  public ResourceAlreadyExistException(String message, Throwable cause) {
    super(message, cause);
  }

  public ResourceAlreadyExistException(Throwable cause) {
    super(cause);
  }

  public ResourceAlreadyExistException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  @Override
  public HttpStatus getHttpStatus() {
    return HttpStatus.CONFLICT;
  }

  @Override
  public boolean isNecessaryToLog() {
    return false;
  }
}
