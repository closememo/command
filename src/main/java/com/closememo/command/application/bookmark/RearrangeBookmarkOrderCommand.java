package com.closememo.command.application.bookmark;

import com.closememo.command.application.Command;
import com.closememo.command.application.CommandRequester;
import com.closememo.command.domain.account.AccountId;
import lombok.Getter;

@Getter
public class RearrangeBookmarkOrderCommand extends Command {

  private final AccountId ownerId;

  public RearrangeBookmarkOrderCommand(CommandRequester requester,
      AccountId ownerId) {
    super(requester);
    this.ownerId = ownerId;
  }
}
