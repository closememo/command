package com.closememo.command.infra.http.mail;

import com.closememo.command.domain.InternalServerException;

public class MailClientException extends InternalServerException {

  private static final long serialVersionUID = -5516577607709368648L;

  public MailClientException() {
  }

  public MailClientException(String message) {
    super(message);
  }
}
