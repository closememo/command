package com.closememo.command.domain.notification;

import java.time.ZonedDateTime;
import java.util.Optional;

public interface NotificationRepository {

  NotificationId nextId();

  Notification save(Notification notification);

  Optional<Notification> findById(NotificationId notificationId);

  boolean existsActiveAndPeriodOverlapped(ZonedDateTime notifyStart, ZonedDateTime notifyEnd);

  void delete(Notification notification);
}
