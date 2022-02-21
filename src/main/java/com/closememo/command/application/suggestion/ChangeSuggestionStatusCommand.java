package com.closememo.command.application.suggestion;

import com.closememo.command.application.ChangeCommand;
import com.closememo.command.application.CommandRequester;
import com.closememo.command.domain.suggestion.Status;
import com.closememo.command.domain.suggestion.SuggestionId;
import lombok.Getter;

@Getter
public class ChangeSuggestionStatusCommand extends ChangeCommand<SuggestionId> {

  private final SuggestionId suggestionId;
  private final Status status;

  public ChangeSuggestionStatusCommand(CommandRequester requester,
      SuggestionId suggestionId, Status status) {
    super(requester, suggestionId);
    this.suggestionId = suggestionId;
    this.status = status;
  }
}
