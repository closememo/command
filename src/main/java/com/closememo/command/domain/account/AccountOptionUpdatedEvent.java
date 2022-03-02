package com.closememo.command.domain.account;

import com.closememo.command.domain.DomainEvent;
import lombok.Getter;

@Getter
public class AccountOptionUpdatedEvent extends DomainEvent {

  private static final long serialVersionUID = -1245866818049110942L;

  private final AccountOption option;

  public AccountOptionUpdatedEvent(AccountId accountId, AccountOption option) {
    super(accountId.getId(), 1);
    this.option = option;
  }
}
