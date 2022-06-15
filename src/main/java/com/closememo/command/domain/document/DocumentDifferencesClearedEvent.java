package com.closememo.command.domain.document;

import com.closememo.command.domain.DomainEvent;
import lombok.Getter;

@Getter
public class DocumentDifferencesClearedEvent extends DomainEvent {

  private static final long serialVersionUID = 803551365137940000L;

  private final DocumentId documentId;

  public DocumentDifferencesClearedEvent(DocumentId documentId) {
    super(documentId.getId(), 1);
    this.documentId = documentId;
  }
}
