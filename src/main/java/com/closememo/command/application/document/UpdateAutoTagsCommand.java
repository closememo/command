package com.closememo.command.application.document;

import com.closememo.command.application.ChangeCommand;
import com.closememo.command.application.CommandRequester;
import com.closememo.command.domain.document.DocumentId;
import java.util.List;
import lombok.Getter;

@Getter
public class UpdateAutoTagsCommand extends ChangeCommand<DocumentId> {

  private final DocumentId documentId;
  private final List<String> autoTags;

  public UpdateAutoTagsCommand(CommandRequester requester,
      DocumentId documentId, List<String> autoTags) {
    super(requester, documentId);
    this.documentId = documentId;
    this.autoTags = autoTags;
  }
}
