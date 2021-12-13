package com.closememo.command.domain.category;

import com.closememo.command.domain.DomainEvent;
import lombok.Getter;

@Getter
public class CategoryUpdatedEvent extends DomainEvent {

  private static final long serialVersionUID = 3308028289241951241L;

  private final String name;

  public CategoryUpdatedEvent(CategoryId categoryId, String name) {
    super(categoryId.getId(), 1);
    this.name = name;
  }
}
