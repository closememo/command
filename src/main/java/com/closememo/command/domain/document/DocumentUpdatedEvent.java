package com.closememo.command.domain.document;

import com.closememo.command.domain.DomainEvent;
import com.closememo.command.domain.account.AccountId;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.Getter;

@Getter
public class DocumentUpdatedEvent extends DomainEvent {

  private static final long serialVersionUID = 2468273243274846169L;

  private final DocumentId documentId;
  private final AccountId ownerId;
  private final String title;
  private final String previousContent;
  private final String content;
  private final List<String> tags;
  private final ZonedDateTime updatedAt;
  private final DocumentOption previousOption;
  private final DocumentOption option;
  private final long previousVersion;

  public DocumentUpdatedEvent(DocumentId documentId, AccountId ownerId, String title,
      String previousContent, String content, List<String> tags, ZonedDateTime updatedAt,
      DocumentOption previousOption, DocumentOption option, long previousVersion) {
    super(documentId.getId(), 1);
    this.documentId = documentId;
    this.ownerId = ownerId;
    this.title = title;
    this.previousContent = previousContent;
    this.content = content;
    this.tags = tags;
    this.updatedAt = updatedAt;
    this.previousOption = previousOption;
    this.option = option;
    this.previousVersion = previousVersion;
  }
}
