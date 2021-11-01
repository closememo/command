package com.closememo.command.infra.http.naver;

import com.closememo.command.domain.InternalServerException;

public class NaverApiInternalServerException extends InternalServerException {

  private static final long serialVersionUID = 3825943711085012508L;

  public NaverApiInternalServerException() {
  }

  public NaverApiInternalServerException(String message) {
    super(message);
  }
}
