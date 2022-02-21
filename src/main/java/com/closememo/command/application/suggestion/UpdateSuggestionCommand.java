package com.closememo.command.application.suggestion;

import com.closememo.command.application.ChangeCommand;
import com.closememo.command.application.CommandRequester;
import com.closememo.command.domain.account.AccountId;
import com.closememo.command.domain.suggestion.SuggestionId;
import lombok.Getter;

@Getter
public class UpdateSuggestionCommand extends ChangeCommand<SuggestionId> {

  private final SuggestionId suggestionId;
  private final AccountId writerId;
  private final String content;

  public UpdateSuggestionCommand(CommandRequester requester,
      SuggestionId suggestionId, AccountId writerId, String content) {
    super(requester, suggestionId);
    this.suggestionId = suggestionId;
    this.writerId = writerId;
    this.content = content;
  }
}
