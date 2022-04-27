package com.closememo.command.config.security.authentication.account;

import com.closememo.command.config.security.authentication.ServiceAuthentication;
import com.closememo.command.domain.account.AccountId;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

public class AccountAuthenticationFilter extends AbstractPreAuthenticatedProcessingFilter {

  public static final String X_ACCOUNT_ID_HEADER_NAME = "X-Account-Id";
  public static final String X_ACCOUNT_ROLE_HEADER_NAME = "X-Account-Roles";

  public AccountAuthenticationFilter(AccountAuthenticationProvider accountAuthenticationProvider) {
    super.setCheckForPrincipalChanges(true);
    super.setAuthenticationManager(new ProviderManager(accountAuthenticationProvider));
  }

  @Override
  protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
    String accountId = request.getHeader(X_ACCOUNT_ID_HEADER_NAME);

    if (StringUtils.isBlank(accountId)) {
      return null;
    }

    List<String> roles = Collections.list(request.getHeaders(X_ACCOUNT_ROLE_HEADER_NAME));
    return new AccountPreAuthentication(new AccountId(accountId), Set.copyOf(roles));
  }

  @Override
  protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
    return StringUtils.EMPTY;
  }

  @Override
  protected boolean principalChanged(HttpServletRequest request,
      Authentication currentAuthentication) {

    if (currentAuthentication instanceof ServiceAuthentication) {
      return false;
    }

    return super.principalChanged(request, currentAuthentication);
  }
}
