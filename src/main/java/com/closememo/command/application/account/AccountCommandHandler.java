package com.closememo.command.application.account;

import com.closememo.command.domain.account.Account;
import com.closememo.command.domain.account.AccountId;
import com.closememo.command.domain.account.AccountNotFoundException;
import com.closememo.command.domain.account.AccountRepository;
import com.closememo.command.domain.account.Social;
import com.closememo.command.domain.account.Token;
import com.closememo.command.infra.http.naver.NaverApiClient;
import com.closememo.command.infra.http.naver.NaverOAuthClient;
import com.closememo.command.infra.http.naver.NaverProfileResponse;
import com.closememo.command.infra.http.naver.NaverTokenResponse;
import com.closememo.command.application.Success;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountCommandHandler {

  private final AccountRepository accountRepository;
  private final NaverApiClient naverApiClient;
  private final NaverOAuthClient naverOAuthClient;

  public AccountCommandHandler(AccountRepository accountRepository,
      NaverApiClient naverApiClient,
      NaverOAuthClient naverOAuthClient) {
    this.accountRepository = accountRepository;
    this.naverApiClient = naverApiClient;
    this.naverOAuthClient = naverOAuthClient;
  }

  @ServiceActivator(inputChannel = "RegisterNaverAccountCommand")
  @Transactional
  public LoginAccount handle(RegisterNaverAccountCommand command) {

    NaverTokenResponse tokenResponse = naverOAuthClient.getAccessToken(
        command.getCode(), command.getState());
    NaverProfileResponse profileResponse = naverApiClient.getNaverProfile(
        tokenResponse.getTokenType(), tokenResponse.getAccessToken());

    String socialId = profileResponse.getNaverProfile().getId();
    String email = profileResponse.getNaverProfile().getEmail();
    Token token = accountRepository.generateNewToken();

    Account account = Account.newOne(accountRepository, Social.NAVER, socialId, email,
        Collections.singletonList(token));

    Account savedAccount = accountRepository.save(account);

    return new LoginAccount(savedAccount.getId(), token);
  }

  @ServiceActivator(inputChannel = "LoginNaverAccountCommand")
  @Transactional
  public LoginAccount handle(LoginNaverAccountCommand command) {

    NaverTokenResponse tokenResponse = naverOAuthClient.getAccessToken(
        command.getCode(), command.getState());
    NaverProfileResponse profileResponse = naverApiClient.getNaverProfile(
        tokenResponse.getTokenType(), tokenResponse.getAccessToken());

    String socialId = profileResponse.getNaverProfile().getId();
    Token token = accountRepository.generateNewToken();

    Account account = accountRepository.findBySocialId(socialId)
        .orElseThrow(AccountNotFoundException::new);

    account.addNewToken(token);
    Account savedAccount = accountRepository.save(account);

    return new LoginAccount(savedAccount.getId(), token);
  }

  @ServiceActivator(inputChannel = "ReissueTokenCommand")
  @Transactional
  public LoginAccount handle(ReissueTokenCommand command) {
    String oldTokenId = command.getTokenId();
    Account account = accountRepository.findByTokenId(oldTokenId)
        .orElseThrow(AccountNotFoundException::new);

    Token oldToken = getToken(account.getTokens(), oldTokenId);

    if (oldToken != null && StringUtils.isNotBlank(oldToken.getChildId())) {
      Token childToken = getToken(account.getTokens(), oldToken.getChildId());
      return new LoginAccount(account.getId(), childToken);
    }

    Token token = accountRepository.generateNewToken();

    account.changeToken(oldTokenId, token);
    Account savedAccount = accountRepository.save(account);

    return new LoginAccount(savedAccount.getId(), token);
  }

  private static Token getToken(List<Token> tokens, String tokenId) {
    return tokens.stream()
        .filter(token -> StringUtils.equals(tokenId, token.getTokenId()))
        .findFirst().orElse(null);
  }

  @ServiceActivator(inputChannel = "LogoutCommand")
  @Transactional
  public AccountId handle(LogoutCommand command) {
    Account account = accountRepository.findById(command.getAccountId())
        .orElseThrow(AccountNotFoundException::new);

    account.removeToken(command.getTokenId());
    Account savedAccount = accountRepository.save(account);

    return savedAccount.getId();
  }

  @ServiceActivator(inputChannel = "ClearTokensCommand")
  @Transactional
  public AccountId handle(ClearTokensCommand command) {
    Account account = accountRepository.findById(command.getAccountId())
        .orElseThrow(AccountNotFoundException::new);

    account.clearTokens();
    Account savedAccount = accountRepository.save(account);

    return savedAccount.getId();
  }

  @ServiceActivator(inputChannel = "WidthdrawAccountCommand")
  @Transactional
  public Success handle(WidthdrawAccountCommand command) {
    Account account = accountRepository.findById(command.getAccountId())
        .orElseThrow(AccountNotFoundException::new);

    account.delete();
    accountRepository.delete(account);
    return Success.getInstance();
  }
}
