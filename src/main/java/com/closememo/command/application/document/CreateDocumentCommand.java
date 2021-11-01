package com.closememo.command.application.document;

import com.closememo.command.domain.account.AccountId;
import com.closememo.command.application.Command;
import com.closememo.command.application.CommandRequester;
import java.util.List;
import lombok.Getter;

@Getter
public class CreateDocumentCommand extends Command {

  private final AccountId ownerId;
  private final String title;
  private final String content;
  private final List<String> tags;

  public CreateDocumentCommand(CommandRequester requester, AccountId ownerId,
      String title, String content, List<String> tags) {
    super(requester);
    this.ownerId = ownerId;
    this.title = title;
    this.content = content;
    this.tags = tags;
  }
}
