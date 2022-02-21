package com.closememo.command.application.category;

import com.closememo.command.application.ChangeCommand;
import com.closememo.command.application.CommandRequester;
import com.closememo.command.domain.category.CategoryId;
import lombok.Getter;

@Getter
public class DeleteCategoryCommand extends ChangeCommand<CategoryId> {

  private final CategoryId categoryId;
  private final Boolean isRootDeleting;

  public DeleteCategoryCommand(CommandRequester requester,
      CategoryId categoryId, Boolean isRootDeleting) {
    super(requester, categoryId);
    this.categoryId = categoryId;
    this.isRootDeleting = isRootDeleting;
  }
}
