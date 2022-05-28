package com.closememo.command.application.notification;

import com.closememo.command.application.Command;
import com.closememo.command.application.CommandRequester;
import java.time.ZonedDateTime;
import lombok.Getter;

@Getter
public class CreateNotificationCommand extends Command {

  private final String title;
  private final String content;
  private final ZonedDateTime notifyStart;
  private final ZonedDateTime notifyEnd;

  public CreateNotificationCommand(CommandRequester requester,
      String title, String content, ZonedDateTime notifyStart, ZonedDateTime notifyEnd) {
    super(requester);
    this.title = title;
    this.content = content;
    this.notifyStart = notifyStart;
    this.notifyEnd = notifyEnd;
  }
}
