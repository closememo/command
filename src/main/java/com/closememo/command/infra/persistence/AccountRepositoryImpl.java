package com.closememo.command.infra.persistence;

import com.closememo.command.domain.account.Account;
import com.closememo.command.domain.account.AccountId;
import com.closememo.command.domain.account.AccountRepository;
import com.closememo.command.domain.account.Token;
import com.closememo.command.infra.persistence.imports.AccountJpaRepository;
import java.time.ZonedDateTime;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class AccountRepositoryImpl implements AccountRepository {

  private final AccountJpaRepository accountJpaRepository;
  private final SequenceGenerator sequenceGenerator;

  public AccountRepositoryImpl(AccountJpaRepository accountJpaRepository,
      SequenceGenerator sequenceGenerator) {
    this.accountJpaRepository = accountJpaRepository;
    this.sequenceGenerator = sequenceGenerator;
  }

  @Override
  public AccountId nextId() {
    return new AccountId(sequenceGenerator.generate());
  }

  @Override
  public Token generateNewToken() {
    ZonedDateTime oneMonthLater = ZonedDateTime.now().plusMonths(1L);
    return new Token(sequenceGenerator.generate(), oneMonthLater.toEpochSecond());
  }

  @Override
  public Account save(Account account) {
    return accountJpaRepository.save(account);
  }

  @Override
  public Optional<Account> findById(AccountId accountId) {
    return accountJpaRepository.findById(accountId);
  }

  @Override
  public Optional<Account> findBySocialId(String socialId) {
    return accountJpaRepository.findBySocialId(socialId);
  }

  @Override
  public Optional<Account> findByUnexpiredTokenId(String tokenId) {
    ZonedDateTime now = ZonedDateTime.now();
    return accountJpaRepository.findByTokensTokenIdAndTokensExpGreaterThanEqual(
        tokenId, now.toEpochSecond());
  }

  @Override
  public boolean existsByEmail(String email) {
    return accountJpaRepository.existsByEmail(email);
  }

  @Override
  public void delete(Account account) {
    accountJpaRepository.delete(account);
  }
}
