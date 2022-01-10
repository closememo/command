package com.closememo.command.application.suggestion;

import com.closememo.command.application.Command;
import com.closememo.command.application.CommandRequester;
import com.closememo.command.domain.account.AccountId;
import lombok.Getter;

@Getter
public class CreateSuggestionCommand extends Command {

  private final AccountId writerId;
  private final String content;

  public CreateSuggestionCommand(CommandRequester requester,
      AccountId writerId, String content) {
    super(requester);
    this.writerId = writerId;
    this.content = content;
  }
}
