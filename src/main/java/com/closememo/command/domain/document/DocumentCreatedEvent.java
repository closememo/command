package com.closememo.command.domain.document;

import com.closememo.command.domain.DomainEvent;
import com.closememo.command.domain.account.AccountId;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.Getter;

@Getter
public class DocumentCreatedEvent extends DomainEvent {

  private static final long serialVersionUID = 2468273243274846169L;

  private final DocumentId documentId;
  private final AccountId ownerId;
  private final String title;
  private final String content;
  private final List<String> tags;
  private final ZonedDateTime createdAt;
  private final DocumentOption option;

  public DocumentCreatedEvent(DocumentId documentId, AccountId ownerId, String title,
      String content, List<String> tags, ZonedDateTime createdAt, DocumentOption option) {

    super(documentId.getId(), 1);
    this.documentId = documentId;
    this.ownerId = ownerId;
    this.title = title;
    this.content = content;
    this.tags = tags;
    this.createdAt = createdAt;
    this.option = option;
  }
}
