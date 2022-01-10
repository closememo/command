package com.closememo.command.infra.persistence.imports;

import com.closememo.command.domain.account.AccountId;
import com.closememo.command.domain.suggestion.Status;
import com.closememo.command.domain.suggestion.Suggestion;
import com.closememo.command.domain.suggestion.SuggestionId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SuggestionJpaRepository extends JpaRepository<Suggestion, SuggestionId> {

  long countByWriterIdAndStatusNot(AccountId writerId, Status status);
}
