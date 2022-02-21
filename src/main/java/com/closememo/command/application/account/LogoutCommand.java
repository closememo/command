package com.closememo.command.application.account;

import com.closememo.command.application.ChangeCommand;
import com.closememo.command.application.CommandRequester;
import com.closememo.command.domain.account.AccountId;
import lombok.Getter;

@Getter
public class LogoutCommand extends ChangeCommand<AccountId> {

  private final AccountId accountId;
  private final String tokenId;

  public LogoutCommand(CommandRequester requester, AccountId accountId, String tokenId) {
    super(requester, accountId);
    this.accountId = accountId;
    this.tokenId = tokenId;
  }
}
