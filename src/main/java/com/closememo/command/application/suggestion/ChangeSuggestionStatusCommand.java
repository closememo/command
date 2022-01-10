package com.closememo.command.application.suggestion;

import com.closememo.command.application.Command;
import com.closememo.command.application.CommandRequester;
import com.closememo.command.domain.suggestion.Status;
import com.closememo.command.domain.suggestion.SuggestionId;
import lombok.Getter;

@Getter
public class ChangeSuggestionStatusCommand extends Command {

  private final SuggestionId suggestionId;
  private final Status status;

  public ChangeSuggestionStatusCommand(CommandRequester requester,
      SuggestionId suggestionId, Status status) {
    super(requester);
    this.suggestionId = suggestionId;
    this.status = status;
  }
}
