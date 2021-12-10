package com.closememo.command.application.document;

import com.closememo.command.application.Command;
import com.closememo.command.application.CommandRequester;
import com.closememo.command.domain.account.AccountId;
import com.closememo.command.interfaces.client.requests.document.CreateDocumentRequest;
import java.util.List;
import lombok.Getter;

@Getter
public class CreateDocumentCommand extends Command {

  private final AccountId ownerId;
  private final String title;
  private final String content;
  private final List<String> tags;
  private final DocumentOption option;

  public CreateDocumentCommand(CommandRequester requester, AccountId ownerId, String title,
      String content, List<String> tags, CreateDocumentRequest.DocumentOption option) {
    super(requester);
    this.ownerId = ownerId;
    this.title = title;
    this.content = content;
    this.tags = tags;
    this.option = option != null ? new DocumentOption(option) : DocumentOption.EMPTY;
  }

  @Getter
  public static class DocumentOption {

    public static final DocumentOption EMPTY = new DocumentOption(false);

    private final Boolean hasAutoTag;

    public DocumentOption(Boolean hasAutoTag) {
      this.hasAutoTag = hasAutoTag;
    }

    public DocumentOption(CreateDocumentRequest.DocumentOption option) {
      this(Boolean.TRUE.equals(option.getHasAutoTag()));
    }
  }
}
