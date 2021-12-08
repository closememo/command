package com.closememo.command.domain.document;

import com.closememo.command.domain.DomainEvent;
import com.closememo.command.domain.account.AccountId;
import java.util.List;
import lombok.Getter;

@Getter
public class AutoTagsUpdatedEvent extends DomainEvent {

  private static final long serialVersionUID = -5330747778223053446L;

  private final DocumentId documentId;
  private final AccountId ownerId;
  private final List<String> autoTags;

  public AutoTagsUpdatedEvent(DocumentId documentId, AccountId ownerId, List<String> autoTags) {

    super(documentId.getId(), 1);
    this.documentId = documentId;
    this.ownerId = ownerId;
    this.autoTags = autoTags;
  }
}
