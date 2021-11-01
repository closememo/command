package com.closememo.command.domain;

import org.springframework.http.HttpStatus;

public abstract class BusinessException extends RuntimeException {

  private static final long serialVersionUID = 4965627663041757655L;

  public BusinessException() {
  }

  public BusinessException(String message) {
    super(message);
  }

  public BusinessException(String message, Throwable cause) {
    super(message, cause);
  }

  public BusinessException(Throwable cause) {
    super(cause);
  }

  public BusinessException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  public abstract HttpStatus getHttpStatus();

  public abstract boolean isNecessaryToLog();
}
