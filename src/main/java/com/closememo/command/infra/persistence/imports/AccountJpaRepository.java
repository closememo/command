package com.closememo.command.infra.persistence.imports;

import com.closememo.command.domain.account.Account;
import com.closememo.command.domain.account.AccountId;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountJpaRepository extends JpaRepository<Account, AccountId> {

  Optional<Account> findBySocialId(String socialId);

  Optional<Account> findByTokensTokenId(String tokenId);

  boolean existsByEmail(String email);
}
