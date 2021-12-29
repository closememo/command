package com.closememo.command.application.category;

import com.closememo.command.application.Command;
import com.closememo.command.application.CommandRequester;
import com.closememo.command.domain.category.CategoryId;
import lombok.Getter;

@Getter
public class DecreaseCategoryCountCommand extends Command {

  private final CategoryId categoryId;

  public DecreaseCategoryCountCommand(CommandRequester requester, CategoryId categoryId) {
    super(requester);
    this.categoryId = categoryId;
  }
}
