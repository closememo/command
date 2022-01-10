package com.closememo.command.infra.persistence;

import com.closememo.command.domain.account.AccountId;
import com.closememo.command.domain.suggestion.Status;
import com.closememo.command.domain.suggestion.Suggestion;
import com.closememo.command.domain.suggestion.SuggestionId;
import com.closememo.command.domain.suggestion.SuggestionRepository;
import com.closememo.command.infra.persistence.imports.SuggestionJpaRepository;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class SuggestionRepositoryImpl implements SuggestionRepository {

  private final SequenceGenerator sequenceGenerator;
  private final SuggestionJpaRepository suggestionJpaRepository;

  public SuggestionRepositoryImpl(
      SequenceGenerator sequenceGenerator,
      SuggestionJpaRepository suggestionJpaRepository) {
    this.sequenceGenerator = sequenceGenerator;
    this.suggestionJpaRepository = suggestionJpaRepository;
  }

  @Override
  public SuggestionId nextId() {
    return new SuggestionId(sequenceGenerator.generate());
  }

  @Override
  public Suggestion save(Suggestion suggestion) {
    return suggestionJpaRepository.save(suggestion);
  }

  @Override
  public long countByWriterIdAndStatusNot(AccountId writerId, Status status) {
    return suggestionJpaRepository.countByWriterIdAndStatusNot(writerId, status);
  }

  @Override
  public Optional<Suggestion> findById(SuggestionId suggestionId) {
    return suggestionJpaRepository.findById(suggestionId);
  }

  @Override
  public void delete(Suggestion suggestion) {
    suggestionJpaRepository.delete(suggestion);
  }
}
