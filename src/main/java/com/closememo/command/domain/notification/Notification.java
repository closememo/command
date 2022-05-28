package com.closememo.command.domain.notification;

import com.closememo.command.domain.Events;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@Entity
@Table(name = "notifications")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification {

  @EmbeddedId
  private NotificationId id;
  @Column(nullable = false, columnDefinition = "VARCHAR(150)")
  private String title;
  @Column(nullable = false, columnDefinition = "TEXT")
  private String content;
  @Column(nullable = false)
  private ZonedDateTime createdAt;
  private ZonedDateTime notifyStart;
  private ZonedDateTime notifyEnd;
  @Column(nullable = false, columnDefinition = "VARCHAR(20)")
  @Enumerated(EnumType.STRING)
  private Status status;

  public Notification(NotificationId id, String title, String content, ZonedDateTime createdAt,
      ZonedDateTime notifyStart, ZonedDateTime notifyEnd, Status status) {
    this.id = id;
    this.title = title;
    this.content = content;
    this.createdAt = createdAt;
    this.notifyStart = notifyStart;
    this.notifyEnd = notifyEnd;
    this.status = status;
  }

  public static Notification newOne(NotificationId id, String title, String content,
      ZonedDateTime notifyStart, ZonedDateTime notifyEnd) {

    validatePeriod(notifyStart, notifyEnd);
    ZonedDateTime createdAt = ZonedDateTime.now();

    Notification notification = new Notification(id, title, content, createdAt,
        notifyStart, notifyEnd, Status.INACTIVE);

    Events.register(new NotificationCreatedEvent(notification.getId(), title, content, createdAt,
        notifyStart, notifyEnd, Status.INACTIVE));
    return notification;
  }

  public void update(String title, String content,
      ZonedDateTime notifyStart, ZonedDateTime notifyEnd) {

    if (this.status.equals(Status.ACTIVE)) {
      throw new CannotUpdateActiveNotificationException("active notification cannot be updated.");
    }

    notifyStart = notifyStart.truncatedTo(ChronoUnit.MINUTES);
    notifyEnd = notifyEnd.truncatedTo(ChronoUnit.MINUTES);
    validatePeriod(notifyStart, notifyEnd);

    if (needNotToUpdate(title, content, notifyStart, notifyEnd)) {
      return;
    }

    this.title = title;
    this.content = content;
    this.notifyStart = notifyStart;
    this.notifyEnd = notifyEnd;

    Events.register(new NotificationUpdatedEvent(this.id, this.title, this.content,
        this.notifyStart, this.notifyEnd));
  }

  private boolean needNotToUpdate(String title, String content,
      ZonedDateTime notifyStart, ZonedDateTime notifyEnd) {

    return StringUtils.equals(this.title, title)
        && StringUtils.equals(this.content, content)
        && this.notifyStart.equals(notifyStart.truncatedTo(ChronoUnit.MINUTES))
        && this.notifyEnd.equals(notifyEnd.truncatedTo(ChronoUnit.MINUTES));
  }

  private static void validatePeriod(ZonedDateTime notifyStart, ZonedDateTime notifyEnd) {
    if (notifyStart.isAfter(notifyEnd)) {
      throw new InvalidPeriodException("the start date must be before the end date.");
    }
  }

  public void activate(NotificationRepository notificationRepository) {
    if (this.status.equals(Status.ACTIVE)) {
      return;
    }
    if (notificationRepository.existsActiveAndPeriodOverlapped(this.notifyStart, this.notifyEnd)) {
      throw new PeriodOverlappedAlreadyExistException();
    }
    this.status = Status.ACTIVE;
    Events.register(new NotificationActivatedEvent(this.id));
  }

  public void inactivate() {
    if (this.status.equals(Status.INACTIVE)) {
      return;
    }
    this.status = Status.INACTIVE;
    Events.register(new NotificationInactivatedEvent(this.id));
  }

  public void delete() {
    Events.register(new NotificationDeletedEvent(this.id));
  }
}
