package com.closememo.command.domain.bookmark;

import com.closememo.command.domain.DomainEvent;
import lombok.Getter;

@Getter
public class BookmarkUpdatedEvent extends DomainEvent {

  private static final long serialVersionUID = -4163523438336511275L;

  private final Integer bookmarkOrder;

  public BookmarkUpdatedEvent(BookmarkId bookmarkId, Integer bookmarkOrder) {
    super(bookmarkId.getId(), 1);
    this.bookmarkOrder = bookmarkOrder;
  }
}
