package com.closememo.command.application.document;

import com.closememo.command.domain.document.DocumentId;
import com.closememo.command.application.Command;
import com.closememo.command.application.CommandRequester;
import java.util.List;
import lombok.Getter;

@Getter
public class UpdateDocumentCommand extends Command {

  private final DocumentId documentId;
  private final String title;
  private final String content;
  private final List<String> tags;

  public UpdateDocumentCommand(CommandRequester requester, DocumentId documentId,
      String title, String content, List<String> tags) {
    super(requester);
    this.documentId = documentId;
    this.title = title;
    this.content = content;
    this.tags = tags;
  }
}
