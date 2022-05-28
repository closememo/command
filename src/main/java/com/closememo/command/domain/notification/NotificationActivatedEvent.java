package com.closememo.command.domain.notification;

import com.closememo.command.domain.DomainEvent;
import lombok.Getter;

@Getter
public class NotificationActivatedEvent extends DomainEvent {

  private static final long serialVersionUID = -3472890838863626500L;

  public NotificationActivatedEvent(NotificationId notificationId) {
    super(notificationId.getId(), 1);
  }
}
