package com.closememo.command.domain.suggestion;

import com.closememo.command.domain.IllegalResourceException;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class InvalidSuggestionContentException extends IllegalResourceException {

  private static final long serialVersionUID = -4956262496027754818L;

  public InvalidSuggestionContentException(String message) {
    super(message);
  }
}
