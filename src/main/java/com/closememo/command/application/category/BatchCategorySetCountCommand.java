package com.closememo.command.application.category;

import com.closememo.command.application.Command;
import com.closememo.command.application.CommandRequester;
import com.closememo.command.domain.category.CategoryId;
import lombok.Getter;

@Getter
public class BatchCategorySetCountCommand extends Command {

  private final CategoryId categoryId;
  private final int count;

  public BatchCategorySetCountCommand(CommandRequester requester,
      CategoryId categoryId, int count) {
    super(requester);
    this.categoryId = categoryId;
    this.count = count;
  }
}
