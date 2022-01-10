package com.closememo.command.domain.suggestion;

import com.closememo.command.domain.IllegalResourceException;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class CannotChangeSuggestionException extends IllegalResourceException {

  private static final long serialVersionUID = 2069277454686203391L;

  public CannotChangeSuggestionException(String message) {
    super(message);
  }
}
