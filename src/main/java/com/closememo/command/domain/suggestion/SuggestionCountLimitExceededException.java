package com.closememo.command.domain.suggestion;

import com.closememo.command.domain.LimitExceededException;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class SuggestionCountLimitExceededException extends LimitExceededException {

  private static final long serialVersionUID = -5430207170905577489L;

  public SuggestionCountLimitExceededException(String message) {
    super(message);
  }
}
