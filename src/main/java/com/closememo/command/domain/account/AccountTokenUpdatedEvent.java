package com.closememo.command.domain.account;

import com.closememo.command.domain.DomainEvent;
import java.util.List;
import lombok.Getter;

@Getter
public class AccountTokenUpdatedEvent extends DomainEvent {

  private static final long serialVersionUID = 6828886201555663254L;

  private final AccountId accountId;
  private final List<Token> tokens;

  public AccountTokenUpdatedEvent(AccountId accountId, List<Token> tokens) {
    super(accountId.getId(), 1);
    this.accountId = accountId;
    this.tokens = tokens;
  }
}
