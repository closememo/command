package com.closememo.command.application.document;

import com.closememo.command.application.Command;
import com.closememo.command.application.CommandRequester;
import com.closememo.command.domain.category.CategoryId;
import com.closememo.command.domain.document.DocumentId;
import java.util.List;
import lombok.Getter;

@Getter
public class ChangeDocumentsCategoryCommand extends Command {

  private final List<DocumentId> documentIds;
  private final CategoryId categoryId;

  public ChangeDocumentsCategoryCommand(CommandRequester requester, List<DocumentId> documentIds,
      CategoryId categoryId) {
    super(requester);
    this.documentIds = documentIds;
    this.categoryId = categoryId;
  }
}
