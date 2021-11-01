package com.closememo.command.application.account;

import com.closememo.command.domain.account.AccountId;
import com.closememo.command.domain.account.Token;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LoginAccount {

  private AccountId accountId;
  private Token token;

  public LoginAccount(AccountId accountId, Token token) {
    this.accountId = accountId;
    this.token = token;
  }
}
