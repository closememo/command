package com.closememo.command.domain.notice;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class NoticeIdTest {

  @Test
  @DisplayName("EqualsAndHashCode 테스트")
  public void testEqualsAndHashCode() {
    NoticeId noticeId1 = new NoticeId("noticeId");
    NoticeId noticeId2 = new NoticeId("noticeId");

    assertEquals(noticeId1, noticeId2);
  }
}
