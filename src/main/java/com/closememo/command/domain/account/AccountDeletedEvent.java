package com.closememo.command.domain.account;

import com.closememo.command.domain.DomainEvent;
import lombok.Getter;

@Getter
public class AccountDeletedEvent extends DomainEvent {

  private static final long serialVersionUID = -7588441233408391018L;

  private final AccountId accountId;

  public AccountDeletedEvent(AccountId accountId) {
    super(accountId.getId(), 1);
    this.accountId = accountId;
  }
}
