package com.closememo.command.domain.account;

import com.closememo.command.domain.IllegalResourceException;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class InvalidEmailException extends IllegalResourceException {

  private static final long serialVersionUID = 7391091364861610L;

  public InvalidEmailException(String message) {
    super(message);
  }
}
