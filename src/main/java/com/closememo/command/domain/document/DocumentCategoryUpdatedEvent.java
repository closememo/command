package com.closememo.command.domain.document;

import com.closememo.command.domain.DomainEvent;
import com.closememo.command.domain.category.CategoryId;
import lombok.Getter;

@Getter
public class DocumentCategoryUpdatedEvent extends DomainEvent {

  private static final long serialVersionUID = 5207759654219295001L;

  private final CategoryId previousCategoryId;
  private final CategoryId categoryId;

  public DocumentCategoryUpdatedEvent(DocumentId documentId,
      CategoryId previousCategoryId, CategoryId categoryId) {
    super(documentId.getId(), 1);
    this.previousCategoryId = previousCategoryId;
    this.categoryId = categoryId;
  }
}
