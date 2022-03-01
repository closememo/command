package com.closememo.command.domain.bookmark;

import com.closememo.command.domain.LimitExceededException;

public class BookmarkCountLimitExceededException extends LimitExceededException {

  private static final long serialVersionUID = -7897580235762921481L;

  public BookmarkCountLimitExceededException(String message) {
    super(message);
  }
}
