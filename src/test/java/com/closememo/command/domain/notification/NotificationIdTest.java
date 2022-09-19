package com.closememo.command.domain.notification;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class NotificationIdTest {

  @Test
  @DisplayName("EqualsAndHashCode 테스트")
  public void testEqualsAndHashCode() {
    NotificationId notificationId1 = new NotificationId("notificationId");
    NotificationId notificationId2 = new NotificationId("notificationId");

    assertEquals(notificationId1, notificationId2);
  }
}
