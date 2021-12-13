package com.closememo.command.domain.category;

import com.closememo.command.domain.IllegalResourceException;

public class InvalidCategoryNameException extends IllegalResourceException {

  private static final long serialVersionUID = -1989205118991905422L;

  public InvalidCategoryNameException(String message) {
    super(message);
  }
}
