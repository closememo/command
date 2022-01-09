package com.closememo.command.domain.document;

import com.closememo.command.domain.DomainEvent;
import com.closememo.command.domain.account.AccountId;
import com.closememo.command.domain.category.CategoryId;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.Getter;

@Getter
public class DocumentCreatedEvent extends DomainEvent {

  private static final long serialVersionUID = 2468273243274846169L;

  private final DocumentId documentId;
  private final AccountId ownerId;
  private final CategoryId categoryId;
  private final String title;
  private final String content;
  private final List<String> tags;
  private final ZonedDateTime createdAt;
  private final DocumentOption option;
  private final Status status;

  public DocumentCreatedEvent(DocumentId documentId, AccountId ownerId, CategoryId categoryId,
      String title, String content, List<String> tags, ZonedDateTime createdAt,
      DocumentOption option, Status status) {

    super(documentId.getId(), 1);
    this.documentId = documentId;
    this.ownerId = ownerId;
    this.categoryId = categoryId;
    this.title = title;
    this.content = content;
    this.tags = tags;
    this.createdAt = createdAt;
    this.option = option;
    this.status = status;
  }
}
