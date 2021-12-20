package com.closememo.command.application.category;

import com.closememo.command.application.Command;
import com.closememo.command.application.CommandRequester;
import com.closememo.command.domain.account.AccountId;
import com.closememo.command.domain.category.CategoryId;
import lombok.Getter;

@Getter
public class CreateCategoryCommand extends Command {

  private final AccountId ownerId;
  private final String name;
  private final CategoryId parentId;

  public CreateCategoryCommand(CommandRequester requester, AccountId ownerId,
      String name, CategoryId parentId) {
    super(requester);
    this.ownerId = ownerId;
    this.name = name;
    this.parentId = parentId;
  }
}
