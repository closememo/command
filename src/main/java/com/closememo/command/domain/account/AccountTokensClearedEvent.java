package com.closememo.command.domain.account;

import com.closememo.command.domain.DomainEvent;
import lombok.Getter;

@Getter
public class AccountTokensClearedEvent extends DomainEvent {

  private static final long serialVersionUID = 7545028765466342162L;

  private final AccountId accountId;

  public AccountTokensClearedEvent(AccountId accountId) {
    super(accountId.getId(), 1);
    this.accountId = accountId;
  }
}
