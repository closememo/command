package com.closememo.command.domain.account;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import com.closememo.command.domain.Events;
import com.closememo.command.domain.account.AccountOption.DocumentOrderType;
import com.closememo.command.domain.category.CategoryId;
import com.closememo.command.infra.sequencegenerator.ObjectId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AccountTest {

  private static final String SOCIAL_ID = "socialId";
  private static final String TEST_ID = "testId";
  private static final String TEST_EMAIL = "test@naver.com";

  @Mock
  private AccountRepository accountRepository;

  @Test
  @DisplayName("잘못된 이메일 포멧 시 예외")
  public void wrongEmailFormat() {
    String wrongEmail = "email";

    assertThrows(InvalidEmailException.class,
        () -> Account.newOne(accountRepository, Social.NAVER, SOCIAL_ID, wrongEmail, Collections.emptyList()));
  }

  @Test
  @DisplayName("너무 긴 이메일 예외")
  public void tooLongEmail() {
    String tooLongEmail = "0123456789".repeat(10) + "@naver.com";

    assertThrows(InvalidEmailException.class,
        () -> Account.newOne(accountRepository, Social.NAVER, SOCIAL_ID, tooLongEmail, Collections.emptyList()));
  }

  @Test
  @DisplayName("이미 존재하는 이메일 예외")
  public void alreadyExistEmail() {
    when(accountRepository.existsByEmail(TEST_EMAIL))
        .thenReturn(Boolean.TRUE);

    assertThrows(EmailAlreadyExistException.class,
        () -> Account.newOne(accountRepository, Social.NAVER, SOCIAL_ID, TEST_EMAIL, Collections.emptyList()));
  }

  @Test
  @DisplayName("Account 생성")
  public void createAccount() {
    initializeAccountRepository();

    try (MockedStatic<Events> mockedStatic = mockStatic(Events.class)) {
      Account account = Account.newOne(accountRepository, Social.NAVER, SOCIAL_ID, TEST_EMAIL, Collections.emptyList());

      assertNotNull(account.getId().getId());
      assertEquals(Social.NAVER, account.getSocial());
      assertEquals(SOCIAL_ID, account.getSocialId());
      assertEquals(TEST_EMAIL, account.getEmail());
      assertEquals(Set.of(Role.USER), account.getRoles());

      mockedStatic.verify(
          () -> Events.register(any(AccountCreatedEvent.class)), times(1));
    }
  }

  @Test
  @DisplayName("Token 하나 추가")
  public void addNewToken() {
    // account 초기화
    initializeAccountRepository();
    Account account = Account.newOne(accountRepository, Social.NAVER, SOCIAL_ID, TEST_EMAIL, Collections.emptyList());

    try (MockedStatic<Events> mockedStatic = mockStatic(Events.class)) {

      String tokenId = generateId();
      Token token = new Token(tokenId, ZonedDateTime.now().toEpochSecond());
      account.addNewToken(token);

      assertEquals(1, account.getTokens().size());
      assertEquals(tokenId, account.getTokens().get(0).getTokenId());

      mockedStatic.verify(
          () -> Events.register(any(AccountTokenUpdatedEvent.class)), times(1));
    }
  }

  @Test
  @DisplayName("Token 최대치가 넘는 경우 trim 처리")
  public void trimTokens() {
    // account 초기화
    initializeAccountRepository();
    List<Token> tokens = generateTokens(Account.NUMBER_OF_TOKEN_LIMIT);
    Account account = Account.newOne(accountRepository, Social.NAVER, SOCIAL_ID, TEST_EMAIL, tokens);

    String tokenId = generateId();
    ZonedDateTime oneMonthLater = ZonedDateTime.now().plusMonths(1L);
    Token token = new Token(tokenId, oneMonthLater.toEpochSecond());
    account.addNewToken(token);

    // NUMBER_OF_TOKEN_LIMIT 만큼 남기고 trim 되어야 한다
    assertEquals(Account.NUMBER_OF_TOKEN_LIMIT, account.getTokens().size());
    // 새로 추가한 token 의 만료 기한이 더 남았기 때문에 tokens 에 존재해야 한다
    assertTrue(account.getTokens().contains(token));
  }

  @Test
  @DisplayName("만료 기한 지난 Token 필터링")
  public void filterTokens() {
    // account 초기화
    initializeAccountRepository();
    List<Token> tokens = generateTokens(5, ZonedDateTime.now().minusHours(1L)); // 만료기한이 1시간 지난 token 들
    Account account = Account.newOne(accountRepository, Social.NAVER, SOCIAL_ID, TEST_EMAIL, tokens);

    String tokenId = generateId();
    ZonedDateTime oneMonthLater = ZonedDateTime.now().plusMonths(1L);
    Token token = new Token(tokenId, oneMonthLater.toEpochSecond());

    account.addNewToken(token);

    assertEquals(1, account.getTokens().size());
    assertTrue(account.getTokens().contains(token));
  }

  @Test
  @DisplayName("Token 변경")
  public void changeToken() {
    int numberOfToken = 5;
    // account 초기화
    initializeAccountRepository();
    List<Token> tokens = generateTokens(numberOfToken);
    Account account = Account.newOne(accountRepository, Social.NAVER, SOCIAL_ID, TEST_EMAIL, tokens);

    try (MockedStatic<Events> mockedStatic = mockStatic(Events.class)) {
      Token oldToken = tokens.get(0);
      String oldTokenId = oldToken.getTokenId();

      String tokenId = generateId();
      ZonedDateTime oneMonthLater = ZonedDateTime.now().plusMonths(1L);
      Token newToken = new Token(tokenId, oneMonthLater.toEpochSecond());

      account.changeToken(oldToken, newToken);

      // 예전 token 은 바로 지워지지 않음
      assertEquals(account.getTokens().size(), numberOfToken + 1);
      // oldToken 은 childId 로 newTokenId 를 가지게 됨
      oldToken = account.getTokens().stream()
          .filter(token -> token.getTokenId().equals(oldTokenId))
          .findFirst().orElseThrow();
      assertEquals(tokenId, oldToken.getChildId());
      // oldToken 의 만료 기한 버퍼는 Account.TOKEN_EXP_BUFFER_SECONDS 를 따름
      assertTrue(oldToken.getExp() <= ZonedDateTime.now().plusSeconds(Account.TOKEN_EXP_BUFFER_SECONDS).toEpochSecond());

      mockedStatic.verify(
          () -> Events.register(any(AccountTokenUpdatedEvent.class)), times(1));
    }
  }

  @Test
  @DisplayName("Token 제거")
  public void removeToken() {
    int numberOfToken = 5;
    // account 초기화
    initializeAccountRepository();
    List<Token> tokens = generateTokens(numberOfToken);
    Account account = Account.newOne(accountRepository, Social.NAVER, SOCIAL_ID, TEST_EMAIL, tokens);

    try (MockedStatic<Events> mockedStatic = mockStatic(Events.class)) {

      Token token = tokens.get(0);
      account.removeToken(token.getTokenId());

      assertEquals(numberOfToken - 1, account.getTokens().size());
      assertFalse(account.getTokens().contains(token));

      mockedStatic.verify(
          () -> Events.register(any(AccountTokenUpdatedEvent.class)), times(1));
    }
  }

  @Test
  @DisplayName("Token 전부 삭제")
  public void clearTokens() {
    int numberOfToken = 5;
    // account 초기화
    initializeAccountRepository();
    List<Token> tokens = generateTokens(numberOfToken);
    Account account = Account.newOne(accountRepository, Social.NAVER, SOCIAL_ID, TEST_EMAIL, tokens);

    try (MockedStatic<Events> mockedStatic = mockStatic(Events.class)) {

      account.clearTokens();

      assertTrue(CollectionUtils.isEmpty(account.getTokens()));

      mockedStatic.verify(
          () -> Events.register(any(AccountTokensClearedEvent.class)), times(1));
    }
  }

  @Test
  @DisplayName("Account 삭제")
  public void delete() {
    initializeAccountRepository();
    Account account = Account.newOne(accountRepository, Social.NAVER, SOCIAL_ID, TEST_EMAIL, Collections.emptyList());

    try (MockedStatic<Events> mockedStatic = mockStatic(Events.class)) {
      account.delete();

      mockedStatic.verify(
          () -> Events.register(any(AccountDeletedEvent.class)), times(1));
    }
  }

  @Test
  @DisplayName("AccountOption 변경")
  public void updateAccountOption() {
    initializeAccountRepository();
    Account account = Account.newOne(accountRepository, Social.NAVER, SOCIAL_ID, TEST_EMAIL, Collections.emptyList());

    try (MockedStatic<Events> mockedStatic = mockStatic(Events.class)) {

      account.updateAccountOption(DocumentOrderType.CREATED_OLDEST, 15);

      AccountOption accountOption = account.getOption();
      assertEquals(DocumentOrderType.CREATED_OLDEST, accountOption.getDocumentOrderType());
      assertEquals(15, accountOption.getDocumentCount());

      mockedStatic.verify(
          () -> Events.register(any(AccountOptionUpdatedEvent.class)), times(1));
    }
  }

  @Test
  @DisplayName("AccountTrack 변경")
  public void updateAccountTrack() {
    String categoryId = "categoryId";

    initializeAccountRepository();
    Account account = Account.newOne(accountRepository, Social.NAVER, SOCIAL_ID, TEST_EMAIL, Collections.emptyList());

    try (MockedStatic<Events> mockedStatic = mockStatic(Events.class)) {

      account.updateAccountTrack(new CategoryId(categoryId));

      AccountTrack accountTrack = account.getTrack();
      assertEquals(categoryId, accountTrack.getRecentlyViewedCategoryId());

      mockedStatic.verify(
          () -> Events.register(any(AccountTrackUpdatedEvent.class)), times(1));
    }
  }

  private void initializeAccountRepository() {
    AccountId accountId = mock(AccountId.class);
    when(accountId.getId())
        .thenReturn(AccountTest.TEST_ID);
    when(accountRepository.nextId())
        .thenReturn(accountId);
  }

  private static String generateId() {
    return new ObjectId().toHexString();
  }

  /**
   * token 을 limit 만큼 생성하여 List 로 반환한다. token 의 만료시간은 1주일 후.
   */
  private static List<Token> generateTokens(int limit) {
    ZonedDateTime oneWeekLater = ZonedDateTime.now().plusWeeks(1L);
    return generateTokens(limit, oneWeekLater);
  }

  /**
   * token 을 limit 만큼 생성하여 List 로 반환한다. token 의 만료시간을 파라미터로 받아 적용한다.
   */
  private static List<Token> generateTokens(int limit, ZonedDateTime exp) {
    List<Token> tokens = new ArrayList<>();
    for (int i = 0; i < limit; i++) {
      tokens.add(new Token(generateId(), exp.toEpochSecond()));
    }
    return tokens;
  }
}
