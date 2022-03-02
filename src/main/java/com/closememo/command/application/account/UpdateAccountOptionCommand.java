package com.closememo.command.application.account;

import com.closememo.command.application.ChangeCommand;
import com.closememo.command.application.CommandRequester;
import com.closememo.command.domain.account.AccountId;
import com.closememo.command.interfaces.client.requests.account.UpdateAccountOptionRequest;
import lombok.Getter;

@Getter
public class UpdateAccountOptionCommand extends ChangeCommand<AccountId> {

  private final AccountId accountId;
  private final DocumentOrderType documentOrderType;
  private final Integer documentCount;

  public UpdateAccountOptionCommand(CommandRequester requester, AccountId accountId,
      UpdateAccountOptionRequest.DocumentOrderType documentOrderType, Integer documentCount) {
    super(requester, accountId);
    this.accountId = accountId;
    this.documentOrderType = (documentOrderType != null)
        ? DocumentOrderType.valueOf(documentOrderType.name()) : null;
    this.documentCount = documentCount;
  }

  enum DocumentOrderType {
    CREATED_NEWEST,
    CREATED_OLDEST,
    UPDATED_NEWEST,
  }
}
