package com.closememo.command.domain.account;

import java.util.Optional;

public interface AccountRepository {

  AccountId nextId();

  Token generateNewToken();

  Account save(Account account);

  Optional<Account> findById(AccountId accountId);

  Optional<Account> findBySocialId(String socialId);

  Optional<Account> findByUnexpiredTokenId(String tokenId);

  boolean existsByEmail(String displayId);

  void delete(Account account);
}
