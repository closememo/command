package com.closememo.command.domain.bookmark;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BookmarkIdTest {

  @Test
  @DisplayName("EqualsAndHashCode 테스트")
  public void testEqualsAndHashCode() {
    BookmarkId bookmarkId1 = new BookmarkId("bookmarkId");
    BookmarkId bookmarkId2 = new BookmarkId("bookmarkId");

    assertEquals(bookmarkId1, bookmarkId2);
  }
}