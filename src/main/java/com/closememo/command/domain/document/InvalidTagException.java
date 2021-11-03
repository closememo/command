package com.closememo.command.domain.document;

import com.closememo.command.domain.IllegalResourceException;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class InvalidTagException extends IllegalResourceException {

  public InvalidTagException(String message) {
    super(message);
  }
}
