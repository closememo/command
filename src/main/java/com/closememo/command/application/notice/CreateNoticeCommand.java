package com.closememo.command.application.notice;

import com.closememo.command.application.Command;
import com.closememo.command.application.CommandRequester;
import lombok.Getter;

@Getter
public class CreateNoticeCommand extends Command {

  private final String title;
  private final String content;

  public CreateNoticeCommand(CommandRequester requester, String title, String content) {
    super(requester);
    this.title = title;
    this.content = content;
  }
}
