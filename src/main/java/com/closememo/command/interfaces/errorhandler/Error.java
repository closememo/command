package com.closememo.command.interfaces.errorhandler;

import lombok.Getter;

@Getter
public class Error {

  private final String type;
  private final String message;

  public Error(String type, String message) {
    this.type = type;
    this.message = message;
  }
}
