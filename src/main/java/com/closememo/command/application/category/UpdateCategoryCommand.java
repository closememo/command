package com.closememo.command.application.category;

import com.closememo.command.application.ChangeCommand;
import com.closememo.command.application.CommandRequester;
import com.closememo.command.domain.category.CategoryId;
import lombok.Getter;

@Getter
public class UpdateCategoryCommand extends ChangeCommand<CategoryId> {

  private final CategoryId categoryId;
  private final String name;

  public UpdateCategoryCommand(CommandRequester requester, CategoryId categoryId, String name) {
    super(requester, categoryId);
    this.categoryId = categoryId;
    this.name = name;
  }
}
