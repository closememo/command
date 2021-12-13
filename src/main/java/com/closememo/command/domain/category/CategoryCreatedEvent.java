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

  public CategoryCreatedEvent(CategoryId categoryId, AccountId ownerId,
      String name, ZonedDateTime createdAt) {

    super(categoryId.getId(), 1);
    this.ownerId = ownerId;
    this.name = name;
    this.createdAt = createdAt;
  }
}
