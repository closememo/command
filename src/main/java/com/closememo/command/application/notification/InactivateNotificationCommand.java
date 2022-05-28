package com.closememo.command.application.notification;

import com.closememo.command.application.Command;
import com.closememo.command.application.CommandRequester;
import com.closememo.command.domain.notification.NotificationId;
import lombok.Getter;

@Getter
public class InactivateNotificationCommand extends Command {

  private final NotificationId notificationId;

  public InactivateNotificationCommand(CommandRequester requester, NotificationId notificationId) {
    super(requester);
    this.notificationId = notificationId;
  }
}
