package com.closememo.command.application.suggestion;

import com.closememo.command.application.Command;
import com.closememo.command.application.CommandRequester;
import com.closememo.command.domain.account.AccountId;
import com.closememo.command.domain.suggestion.SuggestionId;
import lombok.Getter;

@Getter
public class UpdateSuggestionCommand extends Command {

  private final SuggestionId suggestionId;
  private final AccountId writerId;
  private final String content;

  public UpdateSuggestionCommand(CommandRequester requester,
      SuggestionId suggestionId, AccountId writerId, String content) {
    super(requester);
    this.suggestionId = suggestionId;
    this.writerId = writerId;
    this.content = content;
  }
}
