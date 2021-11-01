package com.closememo.command.application.account;

import com.closememo.command.application.Command;
import com.closememo.command.application.CommandRequester;
import lombok.Getter;

@Getter
public class RegisterNaverAccountCommand extends Command {

  private final String code;
  private final String state;

  public RegisterNaverAccountCommand(CommandRequester requester, String code, String state) {
    super(requester);
    this.code = code;
    this.state = state;
  }
}
