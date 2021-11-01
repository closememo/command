package com.closememo.command.domain.document;

import com.closememo.command.domain.IllegalResourceException;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class InvalidTitleException extends IllegalResourceException {

  private static final long serialVersionUID = -1263201478034115476L;

  public InvalidTitleException(String message) {
    super(message);
  }
}
