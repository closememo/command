package com.closememo.command.domain.document;

import com.closememo.command.domain.IllegalResourceException;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class InvalidContentException extends IllegalResourceException {

  private static final long serialVersionUID = 8488628512015671866L;

  public InvalidContentException(String message) {
    super(message);
  }
}
