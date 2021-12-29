package com.closememo.command.domain.category;

import com.closememo.command.domain.IllegalResourceException;

public class CannotDeleteRootCategoryException extends IllegalResourceException {

  private static final long serialVersionUID = -2226034775568785204L;

  public CannotDeleteRootCategoryException(String message) {
    super(message);
  }
}
