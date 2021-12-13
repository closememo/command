package com.closememo.command.domain.category;

import com.closememo.command.domain.DomainEvent;
import lombok.Getter;

@Getter
public class CategoryDeletedEvent extends DomainEvent {

  private static final long serialVersionUID = 3308028289241951241L;

  private final CategoryId categoryId;

  public CategoryDeletedEvent(CategoryId categoryId) {
    super(categoryId.getId(), 1);
    this.categoryId = categoryId;
  }
}
