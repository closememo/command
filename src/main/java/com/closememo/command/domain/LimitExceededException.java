package com.closememo.command.domain;

import org.springframework.http.HttpStatus;

public class LimitExceededException extends BusinessException {

  private static final long serialVersionUID = -93321790607416399L;

  public LimitExceededException() {
  }

  public LimitExceededException(String message) {
    super(message);
  }

  public LimitExceededException(String message, Throwable cause) {
    super(message, cause);
  }

  public LimitExceededException(Throwable cause) {
    super(cause);
  }

  public LimitExceededException(String message, Throwable cause, boolean enableSuppression,
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
