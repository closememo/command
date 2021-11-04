package com.closememo.command.infra.http.mail;

import com.closememo.command.domain.InternalServerException;

public class MailInternalServerException extends InternalServerException {

  private static final long serialVersionUID = 9169882117184171099L;

  public MailInternalServerException() {
  }

  public MailInternalServerException(String message) {
    super(message);
  }
}
