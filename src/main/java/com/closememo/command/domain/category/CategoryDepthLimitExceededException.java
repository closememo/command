package com.closememo.command.domain.category;

import com.closememo.command.domain.LimitExceededException;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class CategoryDepthLimitExceededException extends LimitExceededException {

  private static final long serialVersionUID = -8585261948480077051L;

  public CategoryDepthLimitExceededException(String message) {
    super(message);
  }
}
