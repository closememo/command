package com.closememo.command.domain.notification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import com.closememo.command.domain.Events;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NotificationTest {

  private static final String NOTIFICATION_ID = "notificationId";

  @Mock
  private NotificationRepository notificationRepository;

  @Test
  @DisplayName("Notification 생성시 기간 검증")
  public void validatePeriodWhenCreating() {
    ZonedDateTime wrongNotifyStart = ZonedDateTime.now();
    ZonedDateTime wrongNotifyEnd = ZonedDateTime.now().minus(1L, ChronoUnit.DAYS);

    assertThrows(InvalidPeriodException.class,
        () -> Notification.newOne(new NotificationId(NOTIFICATION_ID),
            "title", "content", wrongNotifyStart, wrongNotifyEnd));
  }

  @Test
  @DisplayName("새 Notification 생성")
  public void createNewNotification() {
    ZonedDateTime notifyStart = ZonedDateTime.now();
    ZonedDateTime notifyEnd = ZonedDateTime.now().plus(1L, ChronoUnit.DAYS);

    try (MockedStatic<Events> mockedStatic = mockStatic(Events.class)) {
      Notification notification = Notification.newOne(new NotificationId(NOTIFICATION_ID),
          "title", "content", notifyStart, notifyEnd);

      assertNotNull(notification.getId().getId());
      assertEquals("title", notification.getTitle());
      assertEquals("content", notification.getContent());
      assertEquals(Status.INACTIVE, notification.getStatus());

      mockedStatic.verify(
          () -> Events.register(any(NotificationCreatedEvent.class)), times(1));
    }
  }

  @Test
  @DisplayName("Notification 수정")
  public void updateNotification() {
    ZonedDateTime notifyStart = ZonedDateTime.now();
    ZonedDateTime notifyEnd = notifyStart.plus(1L, ChronoUnit.DAYS);

    Notification notification = Notification.newOne(new NotificationId(NOTIFICATION_ID),
        "title", "content", notifyStart, notifyEnd);

    try (MockedStatic<Events> mockedStatic = mockStatic(Events.class)) {
      ZonedDateTime newNotifyStart = ZonedDateTime.now().plus(1L, ChronoUnit.DAYS);
      ZonedDateTime newNotifyEnd = newNotifyStart.plus(1L, ChronoUnit.DAYS);

      notification.update("newTitle", "newContent", newNotifyStart, newNotifyEnd);

      assertEquals("newTitle", notification.getTitle());
      assertEquals("newContent", notification.getContent());
      assertEquals(newNotifyStart.truncatedTo(ChronoUnit.MINUTES), notification.getNotifyStart());
      assertEquals(newNotifyEnd.truncatedTo(ChronoUnit.MINUTES), notification.getNotifyEnd());

      mockedStatic.verify(
          () -> Events.register(any(NotificationUpdatedEvent.class)), times(1));
    }
  }

  @Test
  @DisplayName("Notification 수정 - ACTIVE 상태인 경우 예외 발생")
  public void tryUpdateActivatedNotification() {
    ZonedDateTime notifyStart = ZonedDateTime.now();
    ZonedDateTime notifyEnd = notifyStart.plus(1L, ChronoUnit.DAYS);

    Notification notification = Notification.newOne(new NotificationId(NOTIFICATION_ID),
        "title", "content", notifyStart, notifyEnd);

    when(notificationRepository.existsActiveAndPeriodOverlapped(any(), any()))
        .thenReturn(Boolean.FALSE);
    notification.activate(notificationRepository);

    assertThrows(CannotUpdateActiveNotificationException.class,
        () -> notification.update("title", "content", notifyStart, notifyEnd));
  }

  @Test
  @DisplayName("Notification 활성화")
  public void activateNotification() {
    ZonedDateTime notifyStart = ZonedDateTime.now();
    ZonedDateTime notifyEnd = notifyStart.plus(1L, ChronoUnit.DAYS);

    Notification notification = Notification.newOne(new NotificationId(NOTIFICATION_ID),
        "title", "content", notifyStart, notifyEnd);

    when(notificationRepository.existsActiveAndPeriodOverlapped(any(), any()))
        .thenReturn(Boolean.FALSE);

    try (MockedStatic<Events> mockedStatic = mockStatic(Events.class)) {
      notification.activate(notificationRepository);

      assertEquals(Status.ACTIVE, notification.getStatus());

      mockedStatic.verify(
          () -> Events.register(any(NotificationActivatedEvent.class)), times(1));
    }
  }

  @Test
  @DisplayName("Notification 비활성화")
  public void inactivateNotification() {
    ZonedDateTime notifyStart = ZonedDateTime.now();
    ZonedDateTime notifyEnd = notifyStart.plus(1L, ChronoUnit.DAYS);

    Notification notification = Notification.newOne(new NotificationId(NOTIFICATION_ID),
        "title", "content", notifyStart, notifyEnd);

    when(notificationRepository.existsActiveAndPeriodOverlapped(any(), any()))
        .thenReturn(Boolean.FALSE);
    notification.activate(notificationRepository);
    assertEquals(Status.ACTIVE, notification.getStatus());

    try (MockedStatic<Events> mockedStatic = mockStatic(Events.class)) {
      notification.inactivate();

      assertEquals(Status.INACTIVE, notification.getStatus());

      mockedStatic.verify(
          () -> Events.register(any(NotificationInactivatedEvent.class)), times(1));
    }
  }

  @Test
  @DisplayName("Notification 활성화 - 기간이 겹치고 ACTIVE 상태인 다른 notification 이 있으면 예외 발생")
  public void activateNotificationPeriodOverlapped() {
    ZonedDateTime notifyStart = ZonedDateTime.now();
    ZonedDateTime notifyEnd = notifyStart.plus(1L, ChronoUnit.DAYS);

    Notification notification = Notification.newOne(new NotificationId(NOTIFICATION_ID),
        "title", "content", notifyStart, notifyEnd);

    when(notificationRepository.existsActiveAndPeriodOverlapped(any(), any()))
        .thenReturn(Boolean.TRUE);

    assertThrows(PeriodOverlappedAlreadyExistException.class,
        () -> notification.activate(notificationRepository));
  }

  @Test
  @DisplayName("Notification 삭제")
  public void deleteNotification() {
    ZonedDateTime notifyStart = ZonedDateTime.now();
    ZonedDateTime notifyEnd = notifyStart.plus(1L, ChronoUnit.DAYS);

    Notification notification = Notification.newOne(new NotificationId(NOTIFICATION_ID),
        "title", "content", notifyStart, notifyEnd);

    try (MockedStatic<Events> mockedStatic = mockStatic(Events.class)) {
      notification.delete();

      mockedStatic.verify(
          () -> Events.register(any(NotificationDeletedEvent.class)), times(1));
    }
  }
}
