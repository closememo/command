package com.closememo.command.application.document;

import com.closememo.command.domain.document.DocumentId;
import com.closememo.command.application.Command;
import com.closememo.command.application.CommandRequester;
import lombok.Getter;

@Getter
public class DeleteDocumentCommand extends Command {

  private final DocumentId documentId;

  public DeleteDocumentCommand(CommandRequester requester, DocumentId documentId) {
    super(requester);
    this.documentId = documentId;
  }
}
