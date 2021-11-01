package com.closememo.command.domain;

import org.springframework.http.HttpStatus;

public class InternalServerException extends BusinessException {

  private static final long serialVersionUID = -2927877512703050929L;

  public InternalServerException() {
  }

  public InternalServerException(String message) {
    super(message);
  }

  public InternalServerException(String message, Throwable cause) {
    super(message, cause);
  }

  public InternalServerException(Throwable cause) {
    super(cause);
  }

  public InternalServerException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  @Override
  public HttpStatus getHttpStatus() {
    return HttpStatus.INTERNAL_SERVER_ERROR;
  }

  @Override
  public boolean isNecessaryToLog() {
    return true;
  }
}
