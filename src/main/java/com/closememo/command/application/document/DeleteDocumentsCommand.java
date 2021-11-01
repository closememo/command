package com.closememo.command.application.document;

import com.closememo.command.domain.document.DocumentId;
import com.closememo.command.application.Command;
import com.closememo.command.application.CommandRequester;
import java.util.List;
import lombok.Getter;

@Getter
public class DeleteDocumentsCommand extends Command {

  private final List<DocumentId> documentIds;

  public DeleteDocumentsCommand(CommandRequester requester, List<DocumentId> documentIds) {
    super(requester);
    this.documentIds = documentIds;
  }
}
