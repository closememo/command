package com.closememo.command.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  private static final String[] IGNORE_AUTH_URLS = new String[]{
      "/swagger-ui/**", "/swagger-ui.html", "/health-check"
  };

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.csrf().disable()
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
        .headers().frameOptions().disable().and()
        .authorizeRequests().antMatchers(IGNORE_AUTH_URLS).permitAll().and()
        .exceptionHandling().authenticationEntryPoint(http403ForbiddenEntryPoint());
  }

  @Bean
  public AuthenticationEntryPoint http403ForbiddenEntryPoint() {
    return new Http403ForbiddenEntryPoint();
  }
}
