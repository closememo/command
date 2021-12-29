package com.closememo.command.domain.category;

import com.closememo.command.domain.DomainEvent;
import lombok.Getter;

@Getter
public class CategoryCountDecreasedEvent extends DomainEvent {

  private static final long serialVersionUID = 7225228832017295479L;

  public CategoryCountDecreasedEvent(CategoryId categoryId) {
    super(categoryId.getId(), 1);
  }
}
