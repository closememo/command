package com.closememo.command.domain.difference;

import com.closememo.command.domain.DomainEvent;
import com.closememo.command.domain.account.AccountId;
import com.closememo.command.domain.document.DocumentId;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.Getter;

@Getter
public class DifferenceCreatedEvent extends DomainEvent {

  private static final long serialVersionUID = -1180499389199187029L;

  private final DifferenceId differenceId;
  private final AccountId ownerId;
  private final DocumentId documentId;
  private final long documentVersion;
  private final List<LineDelta> lineDeltas;
  private final ZonedDateTime createdAt;

  public DifferenceCreatedEvent(DifferenceId differenceId, AccountId ownerId, DocumentId documentId,
      long documentVersion, List<LineDelta> lineDeltas, ZonedDateTime createdAt) {
    super(differenceId.getId(), 1);
    this.differenceId = differenceId;
    this.ownerId = ownerId;
    this.documentId = documentId;
    this.documentVersion = documentVersion;
    this.lineDeltas = lineDeltas;
    this.createdAt = createdAt;
  }
}
