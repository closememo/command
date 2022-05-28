package com.closememo.command.domain.notification;

import com.closememo.command.domain.DomainEvent;
import java.time.ZonedDateTime;
import lombok.Getter;

@Getter
public class NotificationUpdatedEvent extends DomainEvent {

  private static final long serialVersionUID = -4324628620060879395L;

  private final String title;
  private final String content;
  private final ZonedDateTime notifyStart;
  private final ZonedDateTime notifyEnd;

  public NotificationUpdatedEvent(NotificationId notificationId, String title,
      String content, ZonedDateTime notifyStart, ZonedDateTime notifyEnd) {
    super(notificationId.getId(), 1);
    this.title = title;
    this.content = content;
    this.notifyStart = notifyStart;
    this.notifyEnd = notifyEnd;
  }
}
