package com.closememo.command.application.difference;

import com.closememo.command.domain.difference.DifferenceId;
import com.closememo.command.application.Command;
import com.closememo.command.application.CommandRequester;
import lombok.Getter;

@Getter
public class DeleteDifferenceCommand extends Command {

  private final DifferenceId differenceId;

  public DeleteDifferenceCommand(CommandRequester requester, DifferenceId id) {
    super(requester);
    this.differenceId = id;
  }
}
