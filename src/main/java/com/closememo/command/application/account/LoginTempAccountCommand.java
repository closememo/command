package com.closememo.command.application.account;

import com.closememo.command.application.Command;
import com.closememo.command.application.CommandRequester;
import lombok.Getter;

@Getter
public class LoginTempAccountCommand extends Command {

  private final String ip;

  public LoginTempAccountCommand(CommandRequester requester, String ip) {
    super(requester);
    this.ip = ip;
  }
}
