package com.closememo.command.infra.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.closememo.command.domain.account.Account;
import com.closememo.command.domain.account.AccountId;
import com.closememo.command.domain.account.AccountRepository;
import com.closememo.command.domain.account.Social;
import com.closememo.command.domain.account.Token;
import com.closememo.command.infra.persistence.imports.AccountJpaRepository;
import com.closememo.command.test.ImportSequenceGenerator;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ImportSequenceGenerator
@ExtendWith(SpringExtension.class)
@DataJpaTest
@ActiveProfiles("test")
class AccountRepositoryImplTest {

  private static final String EMAIL = "test@naver.com";
  private static final String SOCIAL_ID = "socialId";

  @Autowired
  private AccountJpaRepository accountJpaRepository;
  @Autowired
  private SequenceGenerator sequenceGenerator;
  private AccountRepository accountRepository;

  @BeforeEach
  public void beforeEach() {
    accountRepository = new AccountRepositoryImpl(accountJpaRepository, sequenceGenerator);
  }

  @AfterEach
  public void afterEach() {
    accountJpaRepository.deleteAll();
  }

  @Test
  @DisplayName("AccountId 생성")
  public void createAccountId() {
    AccountId accountId = accountRepository.nextId();
    assertNotNull(accountId);
  }

  @Test
  @DisplayName("Token 생성")
  public void generateNewToken() {
    Token token = accountRepository.generateNewToken();
    assertNotNull(token);
  }

  @Test
  @DisplayName("Account 저장 후 findById")
  public void saveAndFindById() {
    Token token = accountRepository.generateNewToken();
    Account account = Account.newOne(accountRepository, Social.NAVER,
        SOCIAL_ID, EMAIL, Collections.singletonList(token));
    // 저장
    accountRepository.save(account);
    // findById
    Optional<Account> optionalAccount = accountRepository.findById(account.getId());
    assertTrue(optionalAccount.isPresent());
    // 조회한 Account 값 확인
    Account saved = optionalAccount.get();
    assertEquals(Social.NAVER, saved.getSocial());
    assertEquals(SOCIAL_ID, saved.getSocialId());
    assertEquals(EMAIL, saved.getEmail());
    // 삭제 후 확인
    accountRepository.delete(saved);
    assertTrue(accountRepository.findById(account.getId()).isEmpty());
  }

  @Test
  @DisplayName("Account 저장 후 findBySocialId")
  public void saveAndFindBySocialId() {
    Token token = accountRepository.generateNewToken();
    Account account = Account.newOne(accountRepository, Social.NAVER,
        SOCIAL_ID, EMAIL, Collections.singletonList(token));
    // 저장
    accountRepository.save(account);
    // findBySocialId
    Optional<Account> optionalAccount = accountRepository.findBySocialId(SOCIAL_ID);
    assertTrue(optionalAccount.isPresent());
    // 조회한 Account 값 확인
    Account saved = optionalAccount.get();
    assertEquals(Social.NAVER, saved.getSocial());
    assertEquals(SOCIAL_ID, saved.getSocialId());
    assertEquals(EMAIL, saved.getEmail());
    // 삭제 후 확인
    accountRepository.delete(saved);
    assertTrue(accountRepository.findBySocialId("socialId").isEmpty());
  }

  @Test
  @DisplayName("Account 저장 후 findByUnexpiredTokenId")
  public void saveAndFindByUnexpiredTokenId() {
    Token token = accountRepository.generateNewToken();
    Account account = Account.newOne(accountRepository, Social.NAVER,
        SOCIAL_ID, EMAIL, Collections.singletonList(token));
    // 저장
    accountRepository.save(account);
    // findByUnexpiredTokenId
    Optional<Account> optionalAccount = accountRepository.findByUnexpiredTokenId(token.getTokenId());
    assertTrue(optionalAccount.isPresent());
    // 조회한 Account 값 확인
    Account saved = optionalAccount.get();
    assertEquals(Social.NAVER, saved.getSocial());
    assertEquals(SOCIAL_ID, saved.getSocialId());
    assertEquals(EMAIL, saved.getEmail());
    // 삭제 후 확인
    accountRepository.delete(saved);
    assertTrue(accountRepository.findByUnexpiredTokenId(token.getTokenId()).isEmpty());
  }

  @Test
  @DisplayName("Account 저장 전후로 existsByEmail")
  public void saveAndExistsByEmail() {
    // 저장 전 existsByEmail
    assertFalse(accountRepository.existsByEmail(EMAIL));
    // 저장
    Token token = accountRepository.generateNewToken();
    Account account = Account.newOne(accountRepository, Social.NAVER,
        SOCIAL_ID, EMAIL, Collections.singletonList(token));
    accountRepository.save(account);
    // 저장 후 existsByEmail
    assertTrue(accountRepository.existsByEmail(EMAIL));
    // 삭제 후 existsByEmail
    accountRepository.delete(account);
    assertFalse(accountRepository.existsByEmail(EMAIL));
  }
}
