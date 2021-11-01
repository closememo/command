package com.closememo.command.infra.http.naver;

import com.closememo.command.domain.InternalServerException;

public class NaverOAuthInternalServerException extends InternalServerException {

  private static final long serialVersionUID = 6915492558591841245L;

  public NaverOAuthInternalServerException() {
  }

  public NaverOAuthInternalServerException(String message) {
    super(message);
  }
}
