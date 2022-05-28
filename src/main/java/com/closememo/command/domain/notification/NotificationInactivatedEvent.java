package com.closememo.command.domain.notification;

import com.closememo.command.domain.DomainEvent;
import lombok.Getter;

@Getter
public class NotificationInactivatedEvent extends DomainEvent {

  private static final long serialVersionUID = 4689096446293022624L;

  public NotificationInactivatedEvent(NotificationId notificationId) {
    super(notificationId.getId(), 1);
  }
}
