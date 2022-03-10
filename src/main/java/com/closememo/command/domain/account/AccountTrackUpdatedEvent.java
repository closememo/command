package com.closememo.command.domain.account;

import com.closememo.command.domain.DomainEvent;
import lombok.Getter;

@Getter
public class AccountTrackUpdatedEvent extends DomainEvent {

  private static final long serialVersionUID = -1245866818049110942L;

  private final AccountTrack track;

  public AccountTrackUpdatedEvent(AccountId accountId, AccountTrack track) {
    super(accountId.getId(), 1);
    this.track = track;
  }
}
