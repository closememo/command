package com.closememo.command.application.suggestion;

import com.closememo.command.application.Command;
import com.closememo.command.application.CommandRequester;
import com.closememo.command.domain.suggestion.SuggestionId;
import lombok.Getter;

@Getter
public class DeleteSuggestionCommand extends Command {

  private final SuggestionId suggestionId;

  public DeleteSuggestionCommand(CommandRequester requester,
      SuggestionId suggestionId) {
    super(requester);
    this.suggestionId = suggestionId;
  }
}
