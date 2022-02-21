package com.closememo.command.application.document;

import com.closememo.command.application.ChangeCommand;
import com.closememo.command.application.CommandRequester;
import com.closememo.command.domain.category.CategoryId;
import com.closememo.command.domain.document.DocumentId;
import lombok.Getter;

@Getter
public class ChangeDocumentCategoryCommand extends ChangeCommand<DocumentId> {

  private final DocumentId documentId;
  private final CategoryId categoryId;

  public ChangeDocumentCategoryCommand(CommandRequester requester, DocumentId documentId,
      CategoryId categoryId) {
    super(requester, documentId);
    this.documentId = documentId;
    this.categoryId = categoryId;
  }
}
