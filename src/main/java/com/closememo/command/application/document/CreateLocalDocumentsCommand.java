package com.closememo.command.application.document;

import com.closememo.command.application.Command;
import com.closememo.command.application.CommandRequester;
import com.closememo.command.domain.account.AccountId;
import java.util.List;
import lombok.Getter;

@Getter
public class CreateLocalDocumentsCommand extends Command {

  private final AccountId ownerId;
  private final List<LocalDocument> localDocuments;

  public CreateLocalDocumentsCommand(
      CommandRequester requester, AccountId ownerId, List<LocalDocument> localDocuments) {
    super(requester);
    this.ownerId = ownerId;
    this.localDocuments = localDocuments;
  }

  @Getter
  public static class LocalDocument {

    private final String title;
    private final String content;
    private final String localFormedDateString;

    public LocalDocument(String title, String content, String localFormedDateString) {
      this.title = title;
      this.content = content;
      this.localFormedDateString = localFormedDateString;
    }
  }
}
