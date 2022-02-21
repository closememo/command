package com.closememo.command.application.category;

import com.closememo.command.application.ChangeCommand;
import com.closememo.command.application.CommandRequester;
import com.closememo.command.domain.category.CategoryId;
import lombok.Getter;

@Getter
public class IncreaseCategoryCountCommand extends ChangeCommand<CategoryId> {

  private final CategoryId categoryId;

  public IncreaseCategoryCountCommand(CommandRequester requester, CategoryId categoryId) {
    super(requester, categoryId);
    this.categoryId = categoryId;
  }
}
