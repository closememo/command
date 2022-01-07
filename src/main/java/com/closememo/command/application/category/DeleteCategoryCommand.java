package com.closememo.command.application.category;

import com.closememo.command.application.Command;
import com.closememo.command.application.CommandRequester;
import com.closememo.command.domain.category.CategoryId;
import lombok.Getter;

@Getter
public class DeleteCategoryCommand extends Command {

  private final CategoryId categoryId;
  private final Boolean isRootDeleting;

  public DeleteCategoryCommand(CommandRequester requester,
      CategoryId categoryId, Boolean isRootDeleting) {
    super(requester);
    this.categoryId = categoryId;
    this.isRootDeleting = isRootDeleting;
  }
}
