package com.closememo.command.application;

import com.closememo.command.domain.account.AccountId;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AccountCommandRequester extends CommandRequester {

  @NotNull
  private AccountId accountId;

  public AccountCommandRequester(@NotNull AccountId accountId) {
    super(CommandRequesterType.ACCOUNT);
    this.accountId = accountId;
  }

  public boolean equalsAccount(AccountId accountId) {
    return this.accountId.equals(accountId);
  }
}
