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
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.stereotype.Component;

@Configuration
@EnableMethodSecurity
@EnableWebSecurity
public class SecurityConfig {

  private static final String[] IGNORE_FILTER_URLS = new String[]{
      "/command/swagger-ui/**", "/command/swagger-ui.html", "/health-check"
  };

  private final CustomFilterInitializer customFilterInitializer;

  public SecurityConfig(CustomFilterInitializer customFilterInitializer) {
    this.customFilterInitializer = customFilterInitializer;
  }

  @Bean
  public WebSecurityCustomizer webSecurityCustomizer() {
    return (web) -> web.ignoring().requestMatchers(IGNORE_FILTER_URLS);
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    // filter 추가
    customFilterInitializer.setCustomFilters(http);
    // 기타 설정
    http
        .csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(sessionManagementConfigurer ->
            sessionManagementConfigurer
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .headers(headersConfigurer ->
            headersConfigurer
                .frameOptions(FrameOptionsConfig::disable))
        .exceptionHandling(exceptionHandlingConfigurer ->
            exceptionHandlingConfigurer
                .authenticationEntryPoint(http403ForbiddenEntryPoint()));

    return http.build();
  }

  @Bean
  public AuthenticationEntryPoint http403ForbiddenEntryPoint() {
    return new Http403ForbiddenEntryPoint();
  }

  /**
   * 각 필터를 "@Component" 로 등록할 경우, 자동으로 ApplicationFilterChain 에 등록되게 되고,
   * 위의 WebSecurity.ignoring() 설정에 영향을 받지 않는다.
   * 그래서 각 필터를 직접 HttpSecurity.addFilter 로 등록하도록 한다.
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
      // 순서대로 배치되어야 한다. [system, account, developBypass]
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
