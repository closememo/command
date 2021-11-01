package com.closememo.command.application.account;

import com.closememo.command.domain.account.AccountId;
import com.closememo.command.application.Command;
import com.closememo.command.application.CommandRequester;
import lombok.Getter;

@Getter
public class LogoutCommand extends Command {

  private final AccountId accountId;
  private final String tokenId;

  public LogoutCommand(CommandRequester requester, AccountId accountId, String tokenId) {
    super(requester);
    this.accountId = accountId;
    this.tokenId = tokenId;
  }
}
