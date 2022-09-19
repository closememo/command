package com.closememo.command.domain.bookmark;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import com.closememo.command.domain.Events;
import com.closememo.command.domain.account.AccountId;
import com.closememo.command.domain.document.DocumentId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BookmarkTest {

  private static final String BOOKMARK_ID = "bookmarkId";
  private static final String OWNER_ID = "ownerId";
  private static final String DOCUMENT_ID = "documentId";

  @Mock
  private BookmarkRepository bookmarkRepository;

  @Test
  @DisplayName("Bookmark 등록 제한 초과")
  public void tooManyDocumentsBookmarked() {
    when(bookmarkRepository.countByOwnerId(any(AccountId.class)))
        .thenReturn((long) Bookmark.NUMBER_OF_BOOKMARK_LIMIT);

    assertThrows(BookmarkCountLimitExceededException.class,
        () -> Bookmark.newBookmark(bookmarkRepository, new AccountId(OWNER_ID), new DocumentId(DOCUMENT_ID)));
  }

  @Test
  @DisplayName("Document 가 이미 북마크되어 있음")
  public void documentAlreadyBookmarked() {
    when(bookmarkRepository.existsByDocumentId(any(DocumentId.class)))
        .thenReturn(Boolean.TRUE);

    assertThrows(BookmarkDocumentIdAlreadyExistException.class,
        () -> Bookmark.newBookmark(bookmarkRepository, new AccountId(OWNER_ID), new DocumentId(DOCUMENT_ID)));
  }

  @Test
  @DisplayName("새 Bookmark 생성")
  public void createNewBookmark() {
    initializeBookmarkRepository();

    try (MockedStatic<Events> mockedStatic = mockStatic(Events.class)) {
      Bookmark bookmark = Bookmark.newBookmark(bookmarkRepository, new AccountId(OWNER_ID), new DocumentId(DOCUMENT_ID));

      assertNotNull(bookmark.getId().getId());
      assertEquals(OWNER_ID, bookmark.getOwnerId().getId());
      assertEquals(DOCUMENT_ID, bookmark.getDocumentId().getId());

      mockedStatic.verify(
          () -> Events.register(any(BookmarkCreatedEvent.class)), times(1));
    }
  }

  @Test
  @DisplayName("Bookmark 수정")
  public void updateBookmark() {
    initializeBookmarkRepository();

    Bookmark bookmark = Bookmark.newBookmark(bookmarkRepository, new AccountId(OWNER_ID), new DocumentId(DOCUMENT_ID));

    try (MockedStatic<Events> mockedStatic = mockStatic(Events.class)) {
      assertEquals(1, bookmark.getBookmarkOrder());

      bookmark.update(10);

      assertEquals(10, bookmark.getBookmarkOrder());

      mockedStatic.verify(
          () -> Events.register(any(BookmarkUpdatedEvent.class)), times(1));
    }
  }

  @Test
  @DisplayName("Bookmark 제거")
  public void deleteBookmark() {
    initializeBookmarkRepository();

    Bookmark bookmark = Bookmark.newBookmark(bookmarkRepository, new AccountId(OWNER_ID), new DocumentId(DOCUMENT_ID));

    try (MockedStatic<Events> mockedStatic = mockStatic(Events.class)) {
      bookmark.delete();

      mockedStatic.verify(
          () -> Events.register(any(BookmarkDeletedEvent.class)), times(1));
    }
  }

  private void initializeBookmarkRepository() {
    BookmarkId bookmarkId = mock(BookmarkId.class);
    when(bookmarkId.getId())
        .thenReturn(BOOKMARK_ID);
    when(bookmarkRepository.nextId())
        .thenReturn(bookmarkId);
    when(bookmarkRepository.countByOwnerId(any(AccountId.class)))
        .thenReturn(0L);
    when(bookmarkRepository.existsByDocumentId(any(DocumentId.class)))
        .thenReturn(Boolean.FALSE);
  }
}
