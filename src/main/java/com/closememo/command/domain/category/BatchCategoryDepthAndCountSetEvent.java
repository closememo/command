package com.closememo.command.domain.category;

import com.closememo.command.domain.DomainEvent;
import lombok.Getter;

@Getter
public class BatchCategoryDepthAndCountSetEvent extends DomainEvent {

  private static final long serialVersionUID = 2925315950017506968L;

  private final Integer depth;
  private final Integer count;

  public BatchCategoryDepthAndCountSetEvent(CategoryId categoryId, Integer depth, Integer count) {
    super(categoryId.getId(), 1);
    this.depth = depth;
    this.count = count;
  }
}
