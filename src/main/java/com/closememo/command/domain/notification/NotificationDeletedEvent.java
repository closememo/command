package com.closememo.command.domain.notification;

import com.closememo.command.domain.DomainEvent;
import lombok.Getter;

@Getter
public class NotificationDeletedEvent extends DomainEvent {

  private static final long serialVersionUID = 6539543327415237178L;

  public NotificationDeletedEvent(NotificationId notificationId) {
    super(notificationId.getId(), 1);
  }
}
