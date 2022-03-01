package com.closememo.command.domain.bookmark;

import com.closememo.command.domain.DomainEvent;
import com.closememo.command.domain.account.AccountId;
import lombok.Getter;

@Getter
public class BookmarkDeletedEvent extends DomainEvent {

  private static final long serialVersionUID = 9117677578526321960L;

  private final AccountId ownerId;

  public BookmarkDeletedEvent(BookmarkId bookmarkId, AccountId ownerId) {
    super(bookmarkId.getId(), 1);
    this.ownerId = ownerId;
  }
}
