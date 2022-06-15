package com.closememo.command.application.document;

import com.closememo.command.application.Command;
import com.closememo.command.application.CommandRequester;
import com.closememo.command.domain.document.DocumentId;
import lombok.Getter;

@Getter
public class ClearDifferenceCommand extends Command {

  private final DocumentId documentId;

  public ClearDifferenceCommand(CommandRequester requester,
      DocumentId documentId) {
    super(requester);
    this.documentId = documentId;
  }
}
