package com.closememo.command.infra.persistence;

import com.closememo.command.domain.notification.Notification;
import com.closememo.command.domain.notification.NotificationId;
import com.closememo.command.domain.notification.NotificationRepository;
import com.closememo.command.infra.persistence.imports.NotificationJpaRepository;
import java.time.ZonedDateTime;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class NotificationRepositoryImpl implements NotificationRepository {

  private final SequenceGenerator sequenceGenerator;
  private final NotificationJpaRepository notificationJpaRepository;

  public NotificationRepositoryImpl(
      SequenceGenerator sequenceGenerator,
      NotificationJpaRepository notificationJpaRepository) {
    this.sequenceGenerator = sequenceGenerator;
    this.notificationJpaRepository = notificationJpaRepository;
  }

  @Override
  public NotificationId nextId() {
    return new NotificationId(sequenceGenerator.generate());
  }

  @Override
  public Notification save(Notification notification) {
    return notificationJpaRepository.save(notification);
  }

  @Override
  public Optional<Notification> findById(NotificationId notificationId) {
    return notificationJpaRepository.findById(notificationId);
  }

  @Override
  public boolean existsActiveAndPeriodOverlapped(
      ZonedDateTime notifyStart, ZonedDateTime notifyEnd) {
    return notificationJpaRepository.existsActiveAndPeriodOverlapped(notifyStart, notifyEnd);
  }

  @Override
  public void delete(Notification notification) {
    notificationJpaRepository.delete(notification);
  }
}
