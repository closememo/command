package com.closememo.command.interfaces.errorhandler;

import lombok.Getter;

@Getter
public class ErrorResponse {

  private final Error error;

  public ErrorResponse(Error error) {
    this.error = error;
  }
}
