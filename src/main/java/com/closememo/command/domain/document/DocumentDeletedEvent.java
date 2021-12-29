package com.closememo.command.domain.document;

import com.closememo.command.domain.DomainEvent;
import com.closememo.command.domain.category.CategoryId;
import lombok.Getter;

@Getter
public class DocumentDeletedEvent extends DomainEvent {

  private static final long serialVersionUID = 5482676970634087181L;

  private final DocumentId documentId;
  private final CategoryId categoryId;

  public DocumentDeletedEvent(DocumentId documentId, CategoryId categoryId) {
    super(documentId.getId(), 1);
    this.documentId = documentId;
    this.categoryId = categoryId;
  }
}
