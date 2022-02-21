package com.closememo.command.application.difference;

import com.closememo.command.application.ChangeCommand;
import com.closememo.command.application.CommandRequester;
import com.closememo.command.domain.difference.DifferenceId;
import lombok.Getter;

@Getter
public class DeleteDifferenceCommand extends ChangeCommand<DifferenceId> {

  private final DifferenceId differenceId;

  public DeleteDifferenceCommand(CommandRequester requester, DifferenceId differenceId) {
    super(requester, differenceId);
    this.differenceId = differenceId;
  }
}
