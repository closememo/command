package com.closememo.command.application.category;

import com.closememo.command.application.Command;
import com.closememo.command.application.CommandRequester;
import com.closememo.command.domain.category.CategoryId;
import lombok.Getter;

@Getter
public class UpdateCategoryCommand extends Command {

  private final CategoryId categoryId;
  private final String name;

  public UpdateCategoryCommand(CommandRequester requester, CategoryId categoryId, String name) {
    super(requester);
    this.categoryId = categoryId;
    this.name = name;
  }
}
