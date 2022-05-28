package com.closememo.command.domain.notification;

import com.closememo.command.domain.IllegalResourceException;

public class InvalidPeriodException extends IllegalResourceException {

  private static final long serialVersionUID = 2159189894297074100L;

  public InvalidPeriodException(String message) {
    super(message);
  }
}
