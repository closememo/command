package com.closememo.command.domain.document;

import com.closememo.command.domain.LimitExceededException;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class TagCountLimitExceededException extends LimitExceededException {

  public TagCountLimitExceededException(String message) {
    super(message);
  }
}
