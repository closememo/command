package com.closememo.command.application;

import lombok.Getter;

@Getter
public class SystemCommandRequester extends CommandRequester {

  private static final SystemCommandRequester INSTANCE = new SystemCommandRequester();

  private SystemCommandRequester() {
    super(CommandRequesterType.SYSTEM);
  }

  public static SystemCommandRequester getInstance() {
    return INSTANCE;
  }
}
