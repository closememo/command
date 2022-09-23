package com.closememo.command.infra.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.closememo.command.domain.notification.Notification;
import com.closememo.command.domain.notification.NotificationId;
import com.closememo.command.domain.notification.NotificationRepository;
import com.closememo.command.infra.persistence.imports.NotificationJpaRepository;
import com.closememo.command.test.ImportSequenceGenerator;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ImportSequenceGenerator
@ExtendWith(SpringExtension.class)
@DataJpaTest
@ActiveProfiles("test")
class NotificationRepositoryImplTest {

  @Autowired
  private NotificationJpaRepository notificationJpaRepository;
  @Autowired
  private SequenceGenerator sequenceGenerator;
  private NotificationRepository notificationRepository;

  @BeforeEach
  public void beforeEach() {
    notificationRepository = new NotificationRepositoryImpl(sequenceGenerator, notificationJpaRepository);
  }

  @AfterEach
  public void afterEach() {
    notificationJpaRepository.deleteAll();
  }

  @Test
  @DisplayName("NotificationId 생성")
  public void createNotificationId() {
    NotificationId notificationId = notificationRepository.nextId();
    assertNotNull(notificationId);
  }

  @Test
  @DisplayName("Notification 저장 후 조회 및 삭제")
  public void saveNotificationAndFindById() {
    // 저장
    NotificationId notificationId = notificationRepository.nextId();
    Notification notification = Notification.newOne(notificationId, "title", "content",
        ZonedDateTime.now(), ZonedDateTime.now().plus(1L, ChronoUnit.DAYS));
    notificationRepository.save(notification);
    // 조회
    Optional<Notification> optionalNotification = notificationRepository.findById(notificationId);
    assertTrue(optionalNotification.isPresent());

    Notification saved = optionalNotification.get();
    assertEquals(notificationId, saved.getId());
    // 삭제 후 확인
    notificationRepository.delete(notification);
    assertTrue(notificationRepository.findById(notificationId).isEmpty());
  }

  @Test
  @DisplayName("Notification 저장 전후 기간 겹치는 지 확인 (existsActiveAndPeriodOverlapped)")
  public void saveNotificationAndExistsActiveAndPeriodOverlapped() {
    ZonedDateTime now = ZonedDateTime.now();
    ZonedDateTime oneDayLater = now.plus(1L, ChronoUnit.DAYS);
    // 기간이 겹치는 ACTIVE 상태의 Notification 유무 확인
    assertFalse(notificationRepository.existsActiveAndPeriodOverlapped(now, oneDayLater));
    // 새 Notification 을 ACTIVE 상태로 만들어 저장
    NotificationId notificationId = notificationRepository.nextId();
    Notification notification = Notification.newOne(notificationId, "title", "content",
        now, oneDayLater);
    notification.activate(notificationRepository);
    notificationRepository.save(notification);
    // 기간이 겹치는 ACTIVE 상태의 Notification 유무 확인
    assertTrue(notificationRepository.existsActiveAndPeriodOverlapped(now, oneDayLater));
    // 삭제 후 확인
    notificationRepository.delete(notification);
    assertFalse(notificationRepository.existsActiveAndPeriodOverlapped(now, oneDayLater));
  }
}
