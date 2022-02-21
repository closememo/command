package com.closememo.command.application.document;

import com.closememo.command.application.ChangeCommand;
import com.closememo.command.application.CommandRequester;
import com.closememo.command.domain.document.DocumentId;
import lombok.Getter;

@Getter
public class DeleteDocumentCommand extends ChangeCommand<DocumentId> {

  private final DocumentId documentId;

  public DeleteDocumentCommand(CommandRequester requester, DocumentId documentId) {
    super(requester, documentId);
    this.documentId = documentId;
  }
}
