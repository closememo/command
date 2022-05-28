package com.closememo.command.infra.persistence.imports;

import com.closememo.command.domain.notification.Notification;
import com.closememo.command.domain.notification.NotificationId;
import java.time.ZonedDateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface NotificationJpaRepository extends JpaRepository<Notification, NotificationId> {

  @Query("SELECT CASE WHEN (COUNT(n) > 0) THEN true ELSE false END FROM Notification n"
      + " WHERE n.status = 'ACTIVE' AND (:notifyStart < n.notifyEnd AND n.notifyStart < :notifyEnd)")
  boolean existsActiveAndPeriodOverlapped(ZonedDateTime notifyStart, ZonedDateTime notifyEnd);
}
