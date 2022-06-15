package com.closememo.command.domain.difference;

import com.closememo.command.domain.LimitExceededException;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class DifferenceCountLimitExceededException extends LimitExceededException {

  private static final long serialVersionUID = 6141812420369313291L;

  public DifferenceCountLimitExceededException(String message) {
    super(message);
  }
}
