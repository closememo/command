package com.closememo.command.application.category;

import com.closememo.command.application.Command;
import com.closememo.command.application.CommandRequester;
import com.closememo.command.domain.account.AccountId;
import lombok.Getter;

@Getter
public class CreateRootCategoryCommand extends Command {

  private final AccountId ownerId;

  public CreateRootCategoryCommand(CommandRequester requester,
      AccountId ownerId) {
    super(requester);
    this.ownerId = ownerId;
  }
}
