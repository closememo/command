package com.closememo.command.application.suggestion;

import com.closememo.command.application.Success;
import com.closememo.command.domain.suggestion.Suggestion;
import com.closememo.command.domain.suggestion.SuggestionId;
import com.closememo.command.domain.suggestion.SuggestionNotFoundException;
import com.closememo.command.domain.suggestion.SuggestionRepository;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SuggestionCommandHandler {

  private final SuggestionRepository suggestionRepository;

  public SuggestionCommandHandler(
      SuggestionRepository suggestionRepository) {
    this.suggestionRepository = suggestionRepository;
  }

  @Transactional
  @ServiceActivator(inputChannel = "CreateSuggestionCommand")
  public SuggestionId handle(CreateSuggestionCommand command) {
    Suggestion suggestion = Suggestion.newOne(suggestionRepository,
        command.getWriterId(), command.getContent());

    Suggestion savedSuggestion = suggestionRepository.save(suggestion);
    return savedSuggestion.getId();
  }

  @Transactional
  @ServiceActivator(inputChannel = "UpdateSuggestionCommand")
  public SuggestionId handle(UpdateSuggestionCommand command) {
    Suggestion suggestion = suggestionRepository.findById(command.getSuggestionId())
        .orElseThrow(SuggestionNotFoundException::new);

    suggestion.update(command.getContent());
    Suggestion savedSuggestion = suggestionRepository.save(suggestion);

    return savedSuggestion.getId();
  }

  @Transactional
  @ServiceActivator(inputChannel = "DeleteSuggestionCommand")
  public Success handle(DeleteSuggestionCommand command) {
    Suggestion suggestion = suggestionRepository.findById(command.getSuggestionId())
        .orElseThrow(SuggestionNotFoundException::new);

    suggestion.setDeletedStatus();
    suggestionRepository.save(suggestion);

    return Success.getInstance();
  }
}
