package com.closememo.command.domain.suggestion;

import com.closememo.command.domain.account.AccountId;
import java.util.Optional;

public interface SuggestionRepository {

  SuggestionId nextId();

  Suggestion save(Suggestion suggestion);

  long countByWriterIdAndStatusNot(AccountId writerId, Status status);

  Optional<Suggestion> findById(SuggestionId suggestionId);

  void delete(Suggestion suggestion);
}
