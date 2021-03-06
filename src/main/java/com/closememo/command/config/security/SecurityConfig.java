package com.closememo.command.config.security;

import com.closememo.command.config.security.authentication.account.AccountAuthenticationFilter;
import com.closememo.command.config.security.authentication.account.AccountAuthenticationProvider;
import com.closememo.command.config.security.authentication.bypass.DevelopBypassAuthenticationFilter;
import com.closememo.command.config.security.authentication.system.SystemAuthenticationFilter;
import com.closememo.command.config.security.authentication.system.SystemAuthenticationProvider;
import com.closememo.command.domain.account.AccountRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.stereotype.Component;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  private static final String[] IGNORE_FILTER_URLS = new String[]{
      "/command/swagger-ui/**", "/command/swagger-ui.html", "/health-check"
  };

  private final CustomFilterInitializer customFilterInitializer;

  public SecurityConfig(CustomFilterInitializer customFilterInitializer) {
    this.customFilterInitializer = customFilterInitializer;
  }

  @Override
  public void configure(WebSecurity web) {
    web.ignoring().antMatchers(IGNORE_FILTER_URLS);
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    // filter ??????
    customFilterInitializer.setCustomFilters(http);
    // ?????? ??????
    http
        .csrf().disable()
        .sessionManagement(SessionManagementConfigurer ->
            SessionManagementConfigurer
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .headers(HeadersConfigurer ->
            HeadersConfigurer
                .frameOptions().disable())
        .exceptionHandling(exceptionHandlingConfigurer ->
            exceptionHandlingConfigurer
                .authenticationEntryPoint(http403ForbiddenEntryPoint()));
  }

  @Bean
  public AuthenticationEntryPoint http403ForbiddenEntryPoint() {
    return new Http403ForbiddenEntryPoint();
  }

  /**
   * ??? ????????? "@Component" ??? ????????? ??????, ???????????? ApplicationFilterChain ??? ???????????? ??????,
   * ?????? WebSecurity.ignoring() ????????? ????????? ?????? ?????????.
   * ????????? ??? ????????? ?????? HttpSecurity.addFilter ??? ??????????????? ??????.
   */
  @Component
  public static class CustomFilterInitializer {

    private final static String[] DEV_ENVS = new String[]{"local", "dev"};

    private final AccountAuthenticationProvider accountAuthenticationProvider;
    private final AccountRepository accountRepository;
    private final Environment environment;
    private final SystemAuthenticationProvider systemAuthenticationProvider;

    public CustomFilterInitializer(
        AccountAuthenticationProvider accountAuthenticationProvider,
        AccountRepository accountRepository,
        Environment environment,
        SystemAuthenticationProvider systemAuthenticationProvider) {
      this.accountAuthenticationProvider = accountAuthenticationProvider;
      this.accountRepository = accountRepository;
      this.environment = environment;
      this.systemAuthenticationProvider = systemAuthenticationProvider;
    }

    public AccountAuthenticationFilter accountAuthenticationFilter() {
      return new AccountAuthenticationFilter(accountAuthenticationProvider);
    }

    public DevelopBypassAuthenticationFilter developBypassAuthenticationFilter() {
      return new DevelopBypassAuthenticationFilter(
          accountAuthenticationProvider, accountRepository);
    }

    public SystemAuthenticationFilter systemAuthenticationFilter() {
      return new SystemAuthenticationFilter(systemAuthenticationProvider);
    }

    public void setCustomFilters(HttpSecurity http) {
      // ???????????? ??????????????? ??????. [system, account, developBypass]
      http
          .addFilter(systemAuthenticationFilter())
          .addFilter(accountAuthenticationFilter());

      if (isDevelopingEnvironment()) {
        http.addFilter(developBypassAuthenticationFilter());
      }
    }

    private boolean isDevelopingEnvironment() {
      for (String activeProfile : environment.getActiveProfiles()) {
        for (String devEnv : DEV_ENVS) {
          if (StringUtils.equals(activeProfile, devEnv)) {
            return true;
          }
        }
      }
      return false;
    }
  }
}
