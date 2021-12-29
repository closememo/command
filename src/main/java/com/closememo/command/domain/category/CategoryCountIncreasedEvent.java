package com.closememo.command.domain.category;

import com.closememo.command.domain.DomainEvent;
import lombok.Getter;

@Getter
public class CategoryCountIncreasedEvent extends DomainEvent {

  private static final long serialVersionUID = -3016221895891458510L;

  public CategoryCountIncreasedEvent(CategoryId categoryId) {
    super(categoryId.getId(), 1);
  }
}
