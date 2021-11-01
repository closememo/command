package com.closememo.command.config.security.authentication.bypass;

import com.closememo.command.config.security.authentication.ServiceAuthentication;
import com.closememo.command.config.security.authentication.account.AccountAuthenticationProvider;
import com.closememo.command.domain.account.AccountId;
import com.closememo.command.domain.account.AccountRepository;
import com.closememo.command.config.security.authentication.account.AccountPreAuthentication;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.stereotype.Component;

@Profile({"local", "dev"})
@Order(3)
@Component
public class DevelopBypassAuthenticationFilter extends AbstractPreAuthenticatedProcessingFilter {

  public final static String X_BYPASS_ACCOUNT_ID = "X-Bypass-Account-Id";

  public final AccountRepository accountRepository;

  public DevelopBypassAuthenticationFilter(
      AccountAuthenticationProvider accountAuthenticationProvider,
      AccountRepository accountRepository) {
    this.accountRepository = accountRepository;
    super.setCheckForPrincipalChanges(true);
    super.setAuthenticationManager(new ProviderManager(accountAuthenticationProvider));
  }

  @Override
  protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
    String userIdString = request.getHeader(X_BYPASS_ACCOUNT_ID);

    if (StringUtils.isBlank(userIdString)) {
      return null;
    }

    return accountRepository.findById(new AccountId(userIdString))
        .map(account ->
            new AccountPreAuthentication(
                account.getId(),
                account.getRoles().stream()
                    .map(String::valueOf).collect(Collectors.toSet())))
        .orElse(null);
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
