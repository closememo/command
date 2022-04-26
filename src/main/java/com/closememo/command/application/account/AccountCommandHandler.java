package com.closememo.command.application.account;

import com.closememo.command.application.Success;
import com.closememo.command.domain.account.Account;
import com.closememo.command.domain.account.AccountId;
import com.closememo.command.domain.account.AccountNotFoundException;
import com.closememo.command.domain.account.AccountOption;
import com.closememo.command.domain.account.AccountRepository;
import com.closememo.command.domain.account.Social;
import com.closememo.command.domain.account.Token;
import com.closememo.command.domain.account.TokenNotFoundException;
import com.closememo.command.domain.category.Category;
import com.closememo.command.domain.category.CategoryId;
import com.closememo.command.domain.category.CategoryNotFoundException;
import com.closememo.command.domain.category.CategoryRepository;
import com.closememo.command.infra.http.naver.NaverApiClient;
import com.closememo.command.infra.http.naver.NaverOAuthClient;
import com.closememo.command.infra.http.naver.NaverProfileResponse;
import com.closememo.command.infra.http.naver.NaverTokenResponse;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountCommandHandler {

  private final AccountRepository accountRepository;
  private final CategoryRepository categoryRepository;
  private final NaverApiClient naverApiClient;
  private final NaverOAuthClient naverOAuthClient;

  public AccountCommandHandler(AccountRepository accountRepository,
      CategoryRepository categoryRepository,
      NaverApiClient naverApiClient,
      NaverOAuthClient naverOAuthClient) {
    this.accountRepository = accountRepository;
    this.categoryRepository = categoryRepository;
    this.naverApiClient = naverApiClient;
    this.naverOAuthClient = naverOAuthClient;
  }

  // TODO: 기능을 LoginNaverAccountCommand 로 통합하고 이것은 제거.
  @ServiceActivator(inputChannel = "RegisterNaverAccountCommand")
  @Transactional
  public LoginAccount handle(RegisterNaverAccountCommand command) {

    NaverProfileResponse.NaverProfile profile = getProfileResponse(
        command.getCode(), command.getState());

    String socialId = profile.getId();
    String email = profile.getEmail();
    Token token = accountRepository.generateNewToken();

    Account account = Account.newOne(accountRepository, Social.NAVER, socialId, email,
        Collections.singletonList(token));

    Account savedAccount = accountRepository.save(account);

    return new LoginAccount(savedAccount.getId(), token);
  }

  @ServiceActivator(inputChannel = "LoginTempAccountCommand")
  @Transactional
  public LoginAccount handle(LoginTempAccountCommand command) {
    String ip = command.getIp();
    Token token = accountRepository.generateNewToken();
    Optional<Account> optional = accountRepository.findBySocialId(ip);

    Account account;
    if (optional.isPresent()) {
      // 계정이 존재하면 토큰을 추가한다.
      account = optional.get();
      account.addNewToken(token);
    } else {
      // 없으면 새로 만든다.
      account = Account.newTempOne(accountRepository.nextId(),
          ip, Collections.singletonList(token));
    }
    Account savedAccount = accountRepository.save(account);

    return new LoginAccount(savedAccount.getId(), token);
  }

  @ServiceActivator(inputChannel = "LoginNaverAccountCommand")
  @Transactional
  public LoginAccount handle(LoginNaverAccountCommand command) {

    NaverProfileResponse.NaverProfile profile = getProfileResponse(
        command.getCode(), command.getState());

    String socialId = profile.getId();
    String email = profile.getEmail();
    Token token = accountRepository.generateNewToken();

    Optional<Account> optional = accountRepository.findBySocialId(socialId);

    Account account;
    if (optional.isPresent()) {
      // 계정이 존재하면 토큰을 추가한다.
      account = optional.get();
      account.addNewToken(token);
    } else {
      // 없으면 새로 만든다.
      account = Account.newOne(accountRepository, Social.NAVER, socialId, email,
          Collections.singletonList(token));
    }
    Account savedAccount = accountRepository.save(account);

    return new LoginAccount(savedAccount.getId(), token);
  }

  private NaverProfileResponse.NaverProfile getProfileResponse(String code, String status) {
    NaverTokenResponse tokenResponse = naverOAuthClient.getAccessToken(code, status);
    NaverProfileResponse profileResponse = naverApiClient.getNaverProfile(
        tokenResponse.getTokenType(), tokenResponse.getAccessToken());
    return profileResponse.getNaverProfile();
  }

  @ServiceActivator(inputChannel = "ReissueTokenCommand")
  @Transactional
  public LoginAccount handle(ReissueTokenCommand command) {
    String oldTokenId = command.getTokenId();
    Account account = accountRepository.findByUnexpiredTokenId(oldTokenId)
        .orElseThrow(AccountNotFoundException::new);

    Token oldToken = pickToken(account.getTokens(), oldTokenId);

    // child 가 있다는 것은 곧 삭제될 토큰이라는 뜻이고 이미 changeToken 처리가 되었다는 의미.
    if (StringUtils.isNotBlank(oldToken.getChildId())) {
      Token childToken = pickToken(account.getTokens(), oldToken.getChildId());
      // 별도의 처리 없이 바로 미리 발급한 토큰(childToken)을 반환
      return new LoginAccount(account.getId(), childToken);
    }

    Token newToken = accountRepository.generateNewToken();

    account.changeToken(oldToken, newToken);
    Account savedAccount = accountRepository.save(account);

    return new LoginAccount(savedAccount.getId(), newToken);
  }

  @NonNull
  private static Token pickToken(Collection<Token> tokens, String tokenId) {
    return tokens.stream()
        .filter(token -> StringUtils.equals(tokenId, token.getTokenId()))
        .findFirst()
        .orElseThrow(TokenNotFoundException::new);
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

  @ServiceActivator(inputChannel = "WithdrawAccountCommand")
  @Transactional
  public Success handle(WithdrawAccountCommand command) {
    Account account = accountRepository.findById(command.getAccountId())
        .orElseThrow(AccountNotFoundException::new);

    account.delete();
    accountRepository.delete(account);
    return Success.getInstance();
  }

  @ServiceActivator(inputChannel = "UpdateAccountOptionCommand")
  @Transactional
  public Success handle(UpdateAccountOptionCommand command) {
    Account account = accountRepository.findById(command.getAccountId())
        .orElseThrow(AccountNotFoundException::new);

    AccountOption.DocumentOrderType documentOrderType = (command.getDocumentOrderType() != null)
        ? AccountOption.DocumentOrderType.valueOf(command.getDocumentOrderType().name()) : null;

    account.updateAccountOption(documentOrderType, command.getDocumentCount());
    accountRepository.save(account);

    return Success.getInstance();
  }

  @ServiceActivator(inputChannel = "UpdateAccountTrackCommand")
  @Transactional
  public Success handle(UpdateAccountTrackCommand command) {
    Account account = accountRepository.findById(command.getAccountId())
        .orElseThrow(AccountNotFoundException::new);

    CategoryId categoryId = categoryRepository.findById(command.getRecentlyViewedCategoryId())
        .map(Category::getId)
        .orElseThrow(CategoryNotFoundException::new);

    account.updateAccountTrack(categoryId);
    accountRepository.save(account);

    return Success.getInstance();
  }
}
