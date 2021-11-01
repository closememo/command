package com.closememo.command.domain.document;

import com.closememo.command.domain.DomainEvent;
import lombok.Getter;

@Getter
public class DocumentDeletedEvent extends DomainEvent {

  private static final long serialVersionUID = 5482676970634087181L;

  private final DocumentId documentId;

  public DocumentDeletedEvent(DocumentId documentId) {
    super(documentId.getId(), 1);
    this.documentId = documentId;
  }
}
