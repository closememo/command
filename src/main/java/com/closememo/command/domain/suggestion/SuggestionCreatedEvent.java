package com.closememo.command.domain.suggestion;

import com.closememo.command.domain.DomainEvent;
import com.closememo.command.domain.account.AccountId;
import java.time.ZonedDateTime;
import lombok.Getter;

@Getter
public class SuggestionCreatedEvent extends DomainEvent {

  private static final long serialVersionUID = -4298491202804574925L;

  private final AccountId writerId;
  private final String content;
  private final ZonedDateTime createdAt;
  private final Status status;

  public SuggestionCreatedEvent(SuggestionId suggestionId, AccountId writerId, String content,
      ZonedDateTime createdAt, Status status) {
    super(suggestionId.getId(), 1);
    this.writerId = writerId;
    this.content = content;
    this.createdAt = createdAt;
    this.status = status;
  }
}
