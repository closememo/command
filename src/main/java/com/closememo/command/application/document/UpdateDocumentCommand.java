package com.closememo.command.application.document;

import com.closememo.command.application.Command;
import com.closememo.command.application.CommandRequester;
import com.closememo.command.domain.document.DocumentId;
import com.closememo.command.interfaces.client.requests.document.UpdateDocumentRequest;
import java.util.List;
import lombok.Getter;

@Getter
public class UpdateDocumentCommand extends Command {

  private final DocumentId documentId;
  private final String title;
  private final String content;
  private final List<String> tags;
  private final DocumentOption option;

  public UpdateDocumentCommand(CommandRequester requester, DocumentId documentId, String title,
      String content, List<String> tags, UpdateDocumentRequest.DocumentOption option) {
    super(requester);
    this.documentId = documentId;
    this.title = title;
    this.content = content;
    this.tags = tags;
    this.option = option != null ? new DocumentOption(option) : DocumentOption.EMPTY;
  }

  @Getter
  public static class DocumentOption {

    private static final DocumentOption EMPTY = new DocumentOption(false);

    private final Boolean hasAutoTag;

    public DocumentOption(Boolean hasAutoTag) {
      this.hasAutoTag = hasAutoTag;
    }

    public DocumentOption(UpdateDocumentRequest.DocumentOption option) {
      this(Boolean.TRUE.equals(option.getHasAutoTag()));
    }
  }
}
