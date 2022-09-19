package com.closememo.command.domain.notice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;

import com.closememo.command.domain.Events;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

class NoticeTest {

  private static final String NOTICE_ID = "noticeId";

  @Test
  @DisplayName("Notice 생성")
  public void createNotice() {
    try (MockedStatic<Events> mockedStatic = mockStatic(Events.class)) {
      Notice notice = Notice.newOne(new NoticeId(NOTICE_ID), "title", "content");

      assertNotNull(notice.getId().getId());
      assertEquals("title", notice.getTitle());
      assertEquals("content", notice.getContent());

      mockedStatic.verify(
          () -> Events.register(any(NoticeCreatedEvent.class)), times(1));
    }
  }

  @Test
  @DisplayName("Notice 수정")
  public void updateNotice() {
    Notice notice = Notice.newOne(new NoticeId(NOTICE_ID), "title", "content");

    try (MockedStatic<Events> mockedStatic = mockStatic(Events.class)) {
      notice.update("newTitle", "newContent");

      assertEquals("newTitle", notice.getTitle());
      assertEquals("newContent", notice.getContent());

      mockedStatic.verify(
          () -> Events.register(any(NoticeUpdatedEvent.class)), times(1));
    }
  }

  @Test
  @DisplayName("Notice 삭제")
  public void deleteNotice() {
    Notice notice = Notice.newOne(new NoticeId(NOTICE_ID), "title", "content");

    try (MockedStatic<Events> mockedStatic = mockStatic(Events.class)) {
      notice.delete();

      mockedStatic.verify(
          () -> Events.register(any(NoticeDeletedEvent.class)), times(1));
    }
  }
}
