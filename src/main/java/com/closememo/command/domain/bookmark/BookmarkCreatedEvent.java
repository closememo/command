package com.closememo.command.domain.bookmark;

import com.closememo.command.domain.DomainEvent;
import com.closememo.command.domain.account.AccountId;
import com.closememo.command.domain.document.DocumentId;
import java.time.ZonedDateTime;
import lombok.Getter;

@Getter
public class BookmarkCreatedEvent extends DomainEvent {

  private static final long serialVersionUID = 5136314773359249572L;

  private final AccountId ownerId;
  private final DocumentId documentId;
  private final Integer bookmarkOrder;
  private final ZonedDateTime createdAt;

  public BookmarkCreatedEvent(BookmarkId bookmarkId, AccountId ownerId,
      DocumentId documentId, int bookmarkOrder, ZonedDateTime createdAt) {
    super(bookmarkId.getId(), 1);
    this.ownerId = ownerId;
    this.documentId = documentId;
    this.bookmarkOrder = bookmarkOrder;
    this.createdAt = createdAt;
  }
}
