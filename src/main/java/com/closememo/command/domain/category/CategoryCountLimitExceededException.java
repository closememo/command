package com.closememo.command.domain.category;

import com.closememo.command.domain.LimitExceededException;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class CategoryCountLimitExceededException extends LimitExceededException {

  private static final long serialVersionUID = 3633857770998237012L;

  public CategoryCountLimitExceededException(String message) {
    super(message);
  }
}
