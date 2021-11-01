package com.closememo.command.application;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Success {

  private static final Success INSTANCE = new Success();

  public static Success getInstance() {
    return INSTANCE;
  }
}
