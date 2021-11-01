package com.closememo.command.config.security.authentication;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

public abstract class AbstractAuthenticationProvider implements AuthenticationProvider {

  @Override
  public boolean supports(Class<?> authentication) {
    return PreAuthenticatedAuthenticationToken.class.isAssignableFrom(authentication);
  }
}
