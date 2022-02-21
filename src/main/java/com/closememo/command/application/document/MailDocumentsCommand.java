package com.closememo.command.application.document;

import com.closememo.command.application.ChangeCommand;
import com.closememo.command.application.CommandRequester;
import com.closememo.command.domain.account.AccountId;
import com.closememo.command.domain.document.DocumentId;
import java.util.List;
import lombok.Getter;

@Getter
public class MailDocumentsCommand extends ChangeCommand<DocumentId> {

  private final AccountId accountId;
  private final List<DocumentId> documentIds;
  private final boolean needToDelete;

  public MailDocumentsCommand(CommandRequester requester,
      AccountId accountId, List<DocumentId> documentIds, boolean needToDelete) {
    super(requester, documentIds);
    this.accountId = accountId;
    this.documentIds = documentIds;
    this.needToDelete = needToDelete;
  }
}
