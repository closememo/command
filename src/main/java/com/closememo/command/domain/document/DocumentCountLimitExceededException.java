package com.closememo.command.domain.document;

import com.closememo.command.domain.LimitExceededException;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class DocumentCountLimitExceededException extends LimitExceededException {

  private static final long serialVersionUID = 996273411615115865L;

  public DocumentCountLimitExceededException(String message) {
    super(message);
  }
}
