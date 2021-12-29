package com.closememo.command.domain.category;

import com.closememo.command.domain.DomainEvent;
import com.closememo.command.domain.account.AccountId;
import java.time.ZonedDateTime;
import lombok.Getter;

@Getter
public class CategoryCreatedEvent extends DomainEvent {

  private static final long serialVersionUID = 3308028289241951241L;

  private final AccountId ownerId;
  private final String name;
  private final ZonedDateTime createdAt;
  private final Boolean isRoot;
  private final CategoryId parentId;
  private final Integer depth;
  private final Integer count;

  public CategoryCreatedEvent(CategoryId categoryId, AccountId ownerId, String name,
      ZonedDateTime createdAt, boolean isRoot, CategoryId parentId, int depth, int count) {

    super(categoryId.getId(), 1);
    this.ownerId = ownerId;
    this.name = name;
    this.createdAt = createdAt;
    this.isRoot = isRoot;
    this.parentId = parentId;
    this.depth = depth;
    this.count = count;
  }
}
