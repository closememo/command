package com.closememo.command.application.category;

import com.closememo.command.application.Command;
import com.closememo.command.application.CommandRequester;
import com.closememo.command.domain.account.AccountId;
import lombok.Getter;

@Getter
public class CreateCategoryCommand extends Command {

  private final AccountId ownerId;
  private final String name;

  public CreateCategoryCommand(CommandRequester requester, AccountId ownerId, String name) {
    super(requester);
    this.ownerId = ownerId;
    this.name = name;
  }
}
