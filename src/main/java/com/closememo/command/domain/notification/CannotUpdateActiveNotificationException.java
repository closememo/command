package com.closememo.command.domain.notification;

import com.closememo.command.domain.IllegalResourceException;

public class CannotUpdateActiveNotificationException extends IllegalResourceException {

  private static final long serialVersionUID = 7641037742765942004L;

  public CannotUpdateActiveNotificationException(String message) {
    super(message);
  }
}
