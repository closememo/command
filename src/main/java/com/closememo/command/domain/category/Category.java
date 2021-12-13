package com.closememo.command.domain.category;

import com.closememo.command.domain.Events;
import com.closememo.command.domain.account.AccountId;
import java.time.ZonedDateTime;
import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@Entity
@Table(
    name = "categories",
    indexes = {
        @Index(name = "idx_owner_id", columnList = "ownerId")
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category {

  private static final int MAX_NAME_LENGTH = 100;

  @EmbeddedId
  private CategoryId id;
  @AttributeOverride(name = "id", column = @Column(name = "ownerId", nullable = false))
  private AccountId ownerId;
  @Column(nullable = false, columnDefinition = "VARCHAR(150)")
  private String name;
  @Column(nullable = false)
  private ZonedDateTime createdAt;

  private Category(CategoryId id, AccountId ownerId, String name, ZonedDateTime createdAt) {
    this.id = id;
    this.ownerId = ownerId;
    this.name = name;
    this.createdAt = createdAt;
  }

  public static Category newOne(CategoryRepository categoryRepository,
      AccountId ownerId, String name) {

    validateName(categoryRepository, ownerId, name);

    ZonedDateTime createdAt = ZonedDateTime.now();

    Category category = new Category(categoryRepository.nextId(), ownerId, name, createdAt);
    Events.register(new CategoryCreatedEvent(category.getId(), ownerId, name, createdAt));
    return category;
  }

  public void update(CategoryRepository categoryRepository, String name) {

    validateName(categoryRepository, this.ownerId, name);

    this.name = name;

    Events.register(new CategoryUpdatedEvent(this.id, name));
  }

  private static void validateName(CategoryRepository categoryRepository,
      AccountId ownerId, String name) {

    if (categoryRepository.existsByOwnerIdAndName(ownerId, name)) {
      throw new CategoryNameAlreadyExistException();
    }

    if (StringUtils.isBlank(name)) {
      throw new InvalidCategoryNameException("category name should not be blank");
    }

    if (name.length() > MAX_NAME_LENGTH) {
      throw new InvalidCategoryNameException(
          String.format("category name cannot exceed %d characters", MAX_NAME_LENGTH));
    }
  }

  public void delete() {
    Events.register(new CategoryDeletedEvent(this.id));
  }
}
