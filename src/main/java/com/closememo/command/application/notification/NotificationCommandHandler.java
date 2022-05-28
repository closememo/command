package com.closememo.command.application.notification;

import com.closememo.command.application.Success;
import com.closememo.command.domain.notification.Notification;
import com.closememo.command.domain.notification.NotificationId;
import com.closememo.command.domain.notification.NotificationNotFoundException;
import com.closememo.command.domain.notification.NotificationRepository;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationCommandHandler {

  private final NotificationRepository notificationRepository;

  public NotificationCommandHandler(
      NotificationRepository notificationRepository) {
    this.notificationRepository = notificationRepository;
  }

  @Transactional
  @ServiceActivator(inputChannel = "CreateNotificationCommand")
  public NotificationId handle(CreateNotificationCommand command) {
    NotificationId notificationId = notificationRepository.nextId();

    Notification notification = Notification.newOne(notificationId, command.getTitle(),
        command.getContent(), command.getNotifyStart(), command.getNotifyEnd());

    Notification savedNotification = notificationRepository.save(notification);

    return savedNotification.getId();
  }

  @Transactional
  @ServiceActivator(inputChannel = "UpdateNotificationCommand")
  public NotificationId handle(UpdateNotificationCommand command) {
    Notification notification = notificationRepository.findById(command.getNotificationId())
        .orElseThrow(NotificationNotFoundException::new);

    notification.update(command.getTitle(), command.getContent(),
        command.getNotifyStart(), command.getNotifyEnd());
    Notification savedNotification = notificationRepository.save(notification);

    return savedNotification.getId();
  }

  @Transactional
  @ServiceActivator(inputChannel = "ActivateNotificationCommand")
  public NotificationId handle(ActivateNotificationCommand command) {
    Notification notification = notificationRepository.findById(command.getNotificationId())
        .orElseThrow(NotificationNotFoundException::new);

    notification.activate(notificationRepository);
    Notification savedNotification = notificationRepository.save(notification);

    return savedNotification.getId();
  }

  @Transactional
  @ServiceActivator(inputChannel = "InactivateNotificationCommand")
  public NotificationId handle(InactivateNotificationCommand command) {
    Notification notification = notificationRepository.findById(command.getNotificationId())
        .orElseThrow(NotificationNotFoundException::new);

    notification.inactivate();
    Notification savedNotification = notificationRepository.save(notification);

    return savedNotification.getId();
  }

  @Transactional
  @ServiceActivator(inputChannel = "DeleteNotificationCommand")
  public Success handle(DeleteNotificationCommand command) {
    Notification notification = notificationRepository.findById(command.getNotificationId())
        .orElseThrow(NotificationNotFoundException::new);

    notification.delete();
    notificationRepository.delete(notification);

    return Success.getInstance();
  }
}
