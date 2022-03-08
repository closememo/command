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

  private static final String ROOT_NAME = "메인";
  private static final int NUMBER_OF_CATEGORY_LIMIT = 100;
  private static final int MAX_CATEGORY_DEPTH = 3;
  private static final int MAX_NAME_LENGTH = 100;
  private static final String VALID_CATEGORY_CHARS = "[_\\dA-Za-zㄱ-ㆎ가-힣ힰ-ퟆퟋ-ퟻＡ-Ｚａ-ｚｦ-ﾾￂ-ￇￊ-ￏￒ-ￗￚ-ￜ]+";

  @EmbeddedId
  private CategoryId id;
  @AttributeOverride(name = "id", column = @Column(name = "ownerId", nullable = false))
  private AccountId ownerId;
  @Column(nullable = false, columnDefinition = "VARCHAR(150)")
  private String name;
  @Column(nullable = false)
  private ZonedDateTime createdAt;
  private Boolean isRoot;
  @AttributeOverride(name = "id", column = @Column(name = "parentId"))
  private CategoryId parentId;
  private int depth;
  private int count;

  private Category(CategoryId id, AccountId ownerId, String name, ZonedDateTime createdAt,
      Boolean isRoot, CategoryId parentId, int depth, int count) {
    this.id = id;
    this.ownerId = ownerId;
    this.name = name;
    this.createdAt = createdAt;
    this.isRoot = isRoot;
    this.parentId = parentId;
    this.depth = depth;
    this.count = count;
  }

  public static Category newRootCategory(CategoryId id, AccountId ownerId) {
    ZonedDateTime createdAt = ZonedDateTime.now();

    Category category = new Category(id, ownerId, ROOT_NAME, createdAt,
        true, null, 0, 0);
    Events.register(new CategoryCreatedEvent(category.getId(), ownerId, ROOT_NAME, createdAt,
        true, null, 0, 0));
    return category;
  }

  public static Category newOne(CategoryRepository categoryRepository,
      AccountId ownerId, String name, Category parentCategory) {

    ZonedDateTime createdAt = ZonedDateTime.now();
    CategoryId parentId = parentCategory.getId();
    int depth = parentCategory.getDepth() + 1;

    validateCategoryLimit(categoryRepository, ownerId);
    validateName(categoryRepository, ownerId, parentId, name);
    validateDepth(depth);

    Category category = new Category(categoryRepository.nextId(), ownerId, name, createdAt,
        false, parentCategory.getId(), parentCategory.getDepth() + 1, 0);
    Events.register(new CategoryCreatedEvent(category.getId(), ownerId, name, createdAt,
        false, parentId, depth, 0));
    return category;
  }

  public void update(CategoryRepository categoryRepository, String name) {

    validateName(categoryRepository, this.ownerId, this.parentId, name);

    this.name = name;

    Events.register(new CategoryUpdatedEvent(this.id, name));
  }

  public void increaseCount() {
    this.count += 1;

    Events.register(new CategoryCountIncreasedEvent(this.id));
  }

  public void decreaseCount() {
    this.count -= 1;

    Events.register(new CategoryCountDecreasedEvent(this.id));
  }

  private static void validateCategoryLimit(CategoryRepository categoryRepository,
      AccountId ownerId) {
    if (categoryRepository.countByOwnerId(ownerId) >= NUMBER_OF_CATEGORY_LIMIT) {
      throw new CategoryCountLimitExceededException(
          String.format("the number of documents cannot exceed %d", NUMBER_OF_CATEGORY_LIMIT));
    }
  }

  private static void validateName(CategoryRepository categoryRepository,
      AccountId ownerId, CategoryId parentId, String name) {

    if (categoryRepository.existsByOwnerIdAndParentIdAndName(ownerId, parentId, name)) {
      throw new CategoryNameAlreadyExistException();
    }

    if (StringUtils.isBlank(name)) {
      throw new InvalidCategoryNameException("category name should not be blank");
    }

    if (name.length() > MAX_NAME_LENGTH) {
      throw new InvalidCategoryNameException(
          String.format("category name cannot exceed %d characters", MAX_NAME_LENGTH));
    }

    if (!name.matches(VALID_CATEGORY_CHARS)) {
      throw new InvalidCategoryNameException("category name contains invalid characters");
    }
  }

  private static void validateDepth(int depth) {
    if (depth > MAX_CATEGORY_DEPTH) {
      throw new CategoryDepthLimitExceededException(
          String.format("category depth cannot exceed %d", MAX_CATEGORY_DEPTH));
    }
  }

  public void delete(boolean isRootDeleting) {
    if (isRoot && !isRootDeleting) {
      throw new CannotDeleteRootCategoryException("Cannot delete root category.");
    }

    Events.register(new CategoryDeletedEvent(this.id));
  }
}
