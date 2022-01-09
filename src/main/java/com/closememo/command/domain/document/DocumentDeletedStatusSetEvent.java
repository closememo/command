package com.closememo.command.domain.document;

import com.closememo.command.domain.DomainEvent;
import lombok.Getter;

@Getter
public class DocumentDeletedStatusSetEvent extends DomainEvent {

  private static final long serialVersionUID = 2717106402602563827L;

  private final DocumentId documentId;

  public DocumentDeletedStatusSetEvent(DocumentId documentId) {
    super(documentId.getId(), 1);
    this.documentId = documentId;
  }
}
