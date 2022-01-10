package com.closememo.command.domain.suggestion;

import com.closememo.command.domain.DomainEvent;
import lombok.Getter;

@Getter
public class SuggestionUpdatedEvent extends DomainEvent {

  private static final long serialVersionUID = 8998789400167410252L;

  private final String content;
  private final Status status;

  public SuggestionUpdatedEvent(SuggestionId suggestionId, String content, Status status) {
    super(suggestionId.getId(), 1);
    this.content = content;
    this.status = status;
  }
}
