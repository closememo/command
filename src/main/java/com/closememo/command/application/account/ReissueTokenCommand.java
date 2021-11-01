package com.closememo.command.application.account;

import com.closememo.command.application.Command;
import com.closememo.command.application.CommandRequester;
import lombok.Getter;

@Getter
public class ReissueTokenCommand extends Command {

  private final String tokenId;

  public ReissueTokenCommand(CommandRequester requester,
      String tokenId) {
    super(requester);
    this.tokenId = tokenId;
  }
}
