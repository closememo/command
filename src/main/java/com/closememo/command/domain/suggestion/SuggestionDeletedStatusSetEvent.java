package com.closememo.command.domain.suggestion;

import com.closememo.command.domain.DomainEvent;
import lombok.Getter;

@Getter
public class SuggestionDeletedStatusSetEvent extends DomainEvent {

  private static final long serialVersionUID = -4833924109430922016L;

  public SuggestionDeletedStatusSetEvent(SuggestionId suggestionId) {
    super(suggestionId.getId(), 1);
  }
}
