package com.closememo.command.application.account;

import com.closememo.command.application.ChangeCommand;
import com.closememo.command.application.CommandRequester;
import com.closememo.command.domain.account.AccountId;
import com.closememo.command.domain.category.CategoryId;
import lombok.Getter;

@Getter
public class UpdateAccountTrackCommand extends ChangeCommand<AccountId> {

  private final AccountId accountId;
  private final CategoryId recentlyViewedCategoryId;

  public UpdateAccountTrackCommand(CommandRequester requester, AccountId accountId,
      CategoryId recentlyViewedCategoryId) {
    super(requester, accountId);
    this.accountId = accountId;
    this.recentlyViewedCategoryId = recentlyViewedCategoryId;
  }
}
