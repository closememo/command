package com.closememo.command.config.security.authentication.system;

import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.stereotype.Component;

@Order(1)
@Component
public class SystemAuthenticationFilter extends AbstractPreAuthenticatedProcessingFilter {

  public static final String X_SYSTEM_KEY_HEADER = "X-SYSTEM-KEY";

  public SystemAuthenticationFilter(SystemAuthenticationProvider systemAuthenticationProvider) {
    super.setCheckForPrincipalChanges(true);
    super.setAuthenticationManager(new ProviderManager(systemAuthenticationProvider));
  }

  @Override
  protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
    return request.getHeader(X_SYSTEM_KEY_HEADER);
  }

  @Override
  protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
    return StringUtils.EMPTY;
  }
}
