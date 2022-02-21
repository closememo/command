package com.closememo.command.application.account;

import com.closememo.command.application.ChangeCommand;
import com.closememo.command.application.CommandRequester;
import com.closememo.command.domain.account.AccountId;
import lombok.Getter;

@Getter
public class WithdrawAccountCommand extends ChangeCommand<AccountId> {

  private final AccountId accountId;

  public WithdrawAccountCommand(CommandRequester requester, AccountId accountId) {
    super(requester, accountId);
    this.accountId = accountId;
  }
}
