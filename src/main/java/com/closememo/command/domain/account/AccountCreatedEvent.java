package com.closememo.command.domain.account;

import com.closememo.command.domain.DomainEvent;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;
import lombok.Getter;

@Getter
public class AccountCreatedEvent extends DomainEvent {

  private static final long serialVersionUID = -4105670747539762108L;

  private final AccountId accountId;
  private final String email;
  private final List<Token> tokens;
  private final Set<Role> roles;
  private final AccountOption option;
  private final AccountTrack track;
  private final ZonedDateTime createdAt;

  public AccountCreatedEvent(AccountId accountId, String email, List<Token> tokens,
      Set<Role> roles, AccountOption option, AccountTrack track, ZonedDateTime createdAt) {
    super(accountId.getId(), 1);
    this.accountId = accountId;
    this.email = email;
    this.tokens = tokens;
    this.roles = roles;
    this.option = option;
    this.track = track;
    this.createdAt = createdAt;
  }
}
