package com.closememo.command.config.messaging.integration;

public class AckEventException extends RuntimeException {

  private static final long serialVersionUID = -7833193941138463010L;

  public AckEventException(String message) {
    super(message);
  }
}
