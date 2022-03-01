package com.closememo.command.application.bookmark;

import com.closememo.command.application.Command;
import com.closememo.command.application.CommandRequester;
import com.closememo.command.domain.document.DocumentId;
import lombok.Getter;

@Getter
public class CreateBookmarkCommand extends Command {

  private final DocumentId documentId;

  public CreateBookmarkCommand(CommandRequester requester, DocumentId documentId) {
    super(requester);
    this.documentId = documentId;
  }
}
