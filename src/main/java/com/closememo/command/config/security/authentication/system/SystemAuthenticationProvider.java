package com.closememo.command.config.security.authentication.system;

import com.closememo.command.config.security.authentication.AbstractAuthenticationProvider;
import com.closememo.command.config.security.authentication.ServiceAuthentication;
import java.util.Collections;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SystemAuthenticationProvider extends AbstractAuthenticationProvider {

  private final String systemKey;

  public SystemAuthenticationProvider(@Value("${secret.system-key}") String systemKey) {
    this.systemKey = systemKey;
  }

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    String receivedSystemKey = (String) authentication.getPrincipal();

    if (StringUtils.isEmpty(receivedSystemKey) || !receivedSystemKey.equals(systemKey)) {
      return null;
    }

    log.info("SYSTEM KEY INFO : {}", receivedSystemKey);

    return new ServiceAuthentication("SYSTEM", authentication.getDetails(),
        Collections.singletonList(new SimpleGrantedAuthority("ROLE_SYSTEM")));
  }
}
