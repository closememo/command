package com.closememo.command.domain.suggestion;

import com.closememo.command.domain.DomainEvent;
import lombok.Getter;

@Getter
public class SuggestionDeletedEvent extends DomainEvent {

  private static final long serialVersionUID = -4298491202804574925L;

  public SuggestionDeletedEvent(SuggestionId suggestionId) {
    super(suggestionId.getId(), 1);
  }
}
