package com.closememo.command.domain.difference;

import com.closememo.command.domain.DomainEvent;
import com.closememo.command.domain.document.DocumentId;
import lombok.Getter;

@Getter
public class DifferenceDeletedEvent extends DomainEvent {

  private static final long serialVersionUID = 1162707853428942971L;

  private final DifferenceId differenceId;
  private final DocumentId documentId;

  public DifferenceDeletedEvent(DifferenceId differenceId, DocumentId documentId) {
    super(differenceId.getId(), 1);
    this.differenceId = differenceId;
    this.documentId = documentId;
  }
}
