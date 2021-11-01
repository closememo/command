package com.closememo.command.application.account;

import com.closememo.command.domain.account.AccountId;
import com.closememo.command.application.Command;
import com.closememo.command.application.CommandRequester;
import lombok.Getter;

@Getter
public class ClearTokensCommand extends Command {

  private final AccountId accountId;

  public ClearTokensCommand(CommandRequester requester, AccountId accountId) {
    super(requester);
    this.accountId = accountId;
  }
}
