package com.closememo.command.application;

import lombok.Getter;

@Getter
public class AnonymousCommandRequester extends CommandRequester {

  private static final AnonymousCommandRequester INSTANCE = new AnonymousCommandRequester();

  private AnonymousCommandRequester() {
    super(CommandRequesterType.ANONYMOUS);
  }

  public static AnonymousCommandRequester getInstance() {
    return INSTANCE;
  }
}
