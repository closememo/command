package com.closememo.command.domain.notification;

import com.closememo.command.domain.DomainEvent;
import java.time.ZonedDateTime;
import lombok.Getter;

@Getter
public class NotificationCreatedEvent extends DomainEvent {

  private static final long serialVersionUID = 1739068496675665928L;

  private final String title;
  private final String content;
  private final ZonedDateTime createdAt;
  private final ZonedDateTime notifyStart;
  private final ZonedDateTime notifyEnd;
  private final Status status;

  public NotificationCreatedEvent(NotificationId notificationId, String title,
      String content, ZonedDateTime createdAt, ZonedDateTime notifyStart,
      ZonedDateTime notifyEnd, Status status) {
    super(notificationId.getId(), 1);
    this.title = title;
    this.content = content;
    this.createdAt = createdAt;
    this.notifyStart = notifyStart;
    this.notifyEnd = notifyEnd;
    this.status = status;
  }
}
