package com.closememo.command.domain.bookmark;

import com.closememo.command.domain.Events;
import com.closememo.command.domain.account.AccountId;
import com.closememo.command.domain.document.DocumentId;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.ZonedDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "bookmarks",
    indexes = {
        @Index(name = "idx_owner_id", columnList = "ownerId")
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Bookmark {

  protected static final int NUMBER_OF_BOOKMARK_LIMIT = 100;

  @EmbeddedId
  private BookmarkId id;
  @AttributeOverride(name = "id", column = @Column(name = "ownerId", nullable = false))
  private AccountId ownerId;
  @AttributeOverride(name = "id", column = @Column(name = "documentId", nullable = false))
  private DocumentId documentId;
  private int bookmarkOrder; // 유일성은 DB 가 아니라 코드에서 책임진다.
  @Column(nullable = false)
  private ZonedDateTime createdAt;

  private Bookmark(BookmarkId id, AccountId ownerId,
      DocumentId documentId, int bookmarkOrder, ZonedDateTime createdAt) {
    this.id = id;
    this.ownerId = ownerId;
    this.documentId = documentId;
    this.bookmarkOrder = bookmarkOrder;
    this.createdAt = createdAt;
  }

  public static Bookmark newBookmark(BookmarkRepository bookmarkRepository,
      AccountId ownerId, DocumentId documentId) {

    ZonedDateTime createdAt = ZonedDateTime.now();

    int count = (int) bookmarkRepository.countByOwnerId(ownerId);
    validateBookmarkLimit(count);
    validateDocumentAlreadyExist(bookmarkRepository, documentId);

    Bookmark bookmark = new Bookmark(bookmarkRepository.nextId(), ownerId, documentId,
        count + 1, createdAt);
    Events.register(new BookmarkCreatedEvent(bookmark.getId(), ownerId, documentId,
        count + 1, createdAt).needAck());
    return bookmark;
  }

  private static void validateBookmarkLimit(int count) {
    if (count >= NUMBER_OF_BOOKMARK_LIMIT) {
      throw new BookmarkCountLimitExceededException(
          String.format("the number of bookmarks cannot exceed %d", NUMBER_OF_BOOKMARK_LIMIT));
    }
  }

  private static void validateDocumentAlreadyExist(BookmarkRepository bookmarkRepository,
      DocumentId documentId) {
    if (bookmarkRepository.existsByDocumentId(documentId)) {
      throw new BookmarkDocumentIdAlreadyExistException();
    }
  }

  public void update(int bookmarkOrder) {
    if (this.bookmarkOrder == bookmarkOrder) {
      return;
    }

    this.bookmarkOrder = bookmarkOrder;
    Events.register(new BookmarkUpdatedEvent(this.id, this.bookmarkOrder));
  }

  public void delete() {
    Events.register(new BookmarkDeletedEvent(this.id, this.ownerId).needAck());
  }
}
