package com.closememo.command.application.notification;

import com.closememo.command.application.Command;
import com.closememo.command.application.CommandRequester;
import com.closememo.command.domain.notification.NotificationId;
import java.time.ZonedDateTime;
import lombok.Getter;

@Getter
public class UpdateNotificationCommand extends Command {

  private final NotificationId notificationId;
  private final String title;
  private final String content;
  private final ZonedDateTime notifyStart;
  private final ZonedDateTime notifyEnd;

  public UpdateNotificationCommand(CommandRequester requester, NotificationId notificationId,
      String title, String content, ZonedDateTime notifyStart, ZonedDateTime notifyEnd) {
    super(requester);
    this.notificationId = notificationId;
    this.title = title;
    this.content = content;
    this.notifyStart = notifyStart;
    this.notifyEnd = notifyEnd;
  }
}
