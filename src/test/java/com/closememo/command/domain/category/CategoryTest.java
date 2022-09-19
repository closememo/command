package com.closememo.command.domain.category;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import com.closememo.command.domain.Events;
import com.closememo.command.domain.account.AccountId;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CategoryTest {

  private static final String OWNER_ID = "ownerId";
  private static final String PARENT_CATEGORY_ID = "parentCategoryId";
  private static final String CATEGORY_ID = "categoryId";
  private static final String CATEGORY_NAME = "categoryName";

  @Mock
  private CategoryRepository categoryRepository;

  @Test
  @DisplayName("기등록된 Category 개수가 이미 최대인 경우")
  public void checkCategoryLimit() {
    Category parentCategory = generateMockCategory(PARENT_CATEGORY_ID, 0);

    when(categoryRepository.countByOwnerId(any(AccountId.class)))
        .thenReturn((long) Category.NUMBER_OF_CATEGORY_LIMIT);

    assertThrows(CategoryCountLimitExceededException.class,
        () -> Category.newOne(categoryRepository, new AccountId(OWNER_ID), CATEGORY_NAME, parentCategory));
  }

  @Test
  @DisplayName("생성시 Category 이름 중복 검사")
  public void validCategoryNameWhenCreating() {
    Category parentCategory = generateMockCategory(PARENT_CATEGORY_ID, 0);

    when(categoryRepository.existsByOwnerIdAndParentIdAndName(any(AccountId.class), any(CategoryId.class), anyString()))
        .thenReturn(Boolean.TRUE);

    assertThrows(CategoryNameAlreadyExistException.class,
        () -> Category.newOne(categoryRepository, new AccountId(OWNER_ID), CATEGORY_NAME, parentCategory));
  }

  @Test
  @DisplayName("생성시 Category 이름 검사 - 빈 이름시 예외 발생")
  public void categoryNameEmptyWhenCreating() {
    String emptyCategoryName = StringUtils.EMPTY;

    Category parentCategory = generateMockCategory(PARENT_CATEGORY_ID, 0);

    when(categoryRepository.existsByOwnerIdAndParentIdAndName(any(AccountId.class), any(CategoryId.class), anyString()))
        .thenReturn(Boolean.FALSE);

    assertThrows(InvalidCategoryNameException.class,
        () -> Category.newOne(categoryRepository, new AccountId(OWNER_ID), emptyCategoryName, parentCategory));
  }

  @Test
  @DisplayName("생성시 Category 이름 검사 - 이름이 너무 길면 예외 발생")
  public void categoryNameTooLongWhenCreating() {
    String longCategoryName = "x".repeat(Category.MAX_NAME_LENGTH) + "a";

    Category parentCategory = generateMockCategory(PARENT_CATEGORY_ID, 0);

    when(categoryRepository.existsByOwnerIdAndParentIdAndName(any(AccountId.class), any(CategoryId.class), anyString()))
        .thenReturn(Boolean.FALSE);

    assertThrows(InvalidCategoryNameException.class,
        () -> Category.newOne(categoryRepository, new AccountId(OWNER_ID), longCategoryName, parentCategory));
  }

  @Test
  @DisplayName("생성시 Category 이름 검사 - 허용되지 않는 문자 포함시 예외 발생")
  public void categoryNameWithInvalidCharWhenCreating() {
    String longCategoryName = "\uD83E\uDD17";

    Category parentCategory = generateMockCategory(PARENT_CATEGORY_ID, 0);

    when(categoryRepository.existsByOwnerIdAndParentIdAndName(any(AccountId.class), any(CategoryId.class), anyString()))
        .thenReturn(Boolean.FALSE);

    assertThrows(InvalidCategoryNameException.class,
        () -> Category.newOne(categoryRepository, new AccountId(OWNER_ID), longCategoryName, parentCategory));
  }

  @Test
  @DisplayName("생성시 Category depth 검사")
  public void categoryDepthTooLongWhenCreating() {
    int parentCategoryDepth = Category.MAX_CATEGORY_DEPTH;

    Category parentCategory = generateMockCategory(PARENT_CATEGORY_ID, parentCategoryDepth);

    when(categoryRepository.existsByOwnerIdAndParentIdAndName(any(AccountId.class), any(CategoryId.class), anyString()))
        .thenReturn(Boolean.FALSE);

    assertThrows(CategoryDepthLimitExceededException.class,
        () -> Category.newOne(categoryRepository, new AccountId(OWNER_ID), CATEGORY_NAME, parentCategory));
  }

  @Test
  @DisplayName("root Category 생성")
  public void createNewRootCategory() {
    try (MockedStatic<Events> mockedStatic = mockStatic(Events.class)) {
      Category rootCategory = Category.newRootCategory(new CategoryId(CATEGORY_ID), new AccountId(OWNER_ID));

      assertEquals(OWNER_ID, rootCategory.getOwnerId().getId());
      assertEquals(Category.ROOT_NAME, rootCategory.getName());
      assertTrue(rootCategory.getIsRoot());

      mockedStatic.verify(
          () -> Events.register(any(CategoryCreatedEvent.class)), times(1));
    }
  }

  @Test
  @DisplayName("일반 Category 생성")
  public void createNewCategory() {
    initializeBookmarkRepository();

    Category parentCategory = generateMockCategory(PARENT_CATEGORY_ID, 0);

    try (MockedStatic<Events> mockedStatic = mockStatic(Events.class)) {
      Category category = Category.newOne(categoryRepository, new AccountId(OWNER_ID), CATEGORY_NAME, parentCategory);

      assertEquals(OWNER_ID, category.getOwnerId().getId());
      assertEquals(CATEGORY_NAME, category.getName());
      assertFalse(category.getIsRoot());

      mockedStatic.verify(
          () -> Events.register(any(CategoryCreatedEvent.class)), times(1));
    }
  }

  @Test
  @DisplayName("Category 수정")
  public void updateCategory() {
    String newCategoryName = "newCategoryName";
    initializeBookmarkRepository();

    Category parentCategory = generateMockCategory(PARENT_CATEGORY_ID, 0);
    Category category = Category.newOne(categoryRepository, new AccountId(OWNER_ID), CATEGORY_NAME, parentCategory);

    try (MockedStatic<Events> mockedStatic = mockStatic(Events.class)) {
      category.update(categoryRepository, newCategoryName);

      assertEquals(newCategoryName, category.getName());

      mockedStatic.verify(
          () -> Events.register(any(CategoryUpdatedEvent.class)), times(1));
    }
  }

  @Test
  @DisplayName("Category count 증가")
  public void increaseCategoryCount() {
    initializeBookmarkRepository();

    Category parentCategory = generateMockCategory(PARENT_CATEGORY_ID, 0);
    Category category = Category.newOne(categoryRepository, new AccountId(OWNER_ID), CATEGORY_NAME, parentCategory);

    assertEquals(0, category.getCount());

    try (MockedStatic<Events> mockedStatic = mockStatic(Events.class)) {
      category.increaseCount();

      assertEquals(1, category.getCount());

      mockedStatic.verify(
          () -> Events.register(any(CategoryCountIncreasedEvent.class)), times(1));
    }
  }

  @Test
  @DisplayName("Category count 감소")
  public void decreaseCategoryCount() {
    initializeBookmarkRepository();

    Category parentCategory = generateMockCategory(PARENT_CATEGORY_ID, 0);
    Category category = Category.newOne(categoryRepository, new AccountId(OWNER_ID), CATEGORY_NAME, parentCategory);
    category.increaseCount();

    assertEquals(1, category.getCount());

    try (MockedStatic<Events> mockedStatic = mockStatic(Events.class)) {
      category.decreaseCount();

      assertEquals(0, category.getCount());

      mockedStatic.verify(
          () -> Events.register(any(CategoryCountDecreasedEvent.class)), times(1));
    }
  }

  @Test
  @DisplayName("Category 삭제")
  public void deleteCategory() {
    boolean isRootDeleting = false;
    initializeBookmarkRepository();

    Category parentCategory = generateMockCategory(PARENT_CATEGORY_ID, 0);
    Category category = Category.newOne(categoryRepository, new AccountId(OWNER_ID), CATEGORY_NAME, parentCategory);

    try (MockedStatic<Events> mockedStatic = mockStatic(Events.class)) {
      category.delete(isRootDeleting);

      mockedStatic.verify(
          () -> Events.register(any(CategoryDeletedEvent.class)), times(1));
    }
  }

  @Test
  @DisplayName("root Category 삭제시 예외 발생")
  public void deleteRootCategory() {
    boolean isRootDeleting = false; // 원래는 root Category 의 경우 명시적으로 isRootDeleting 으로 true 를 넘겨야하지만 실수로 false 라고 쓰더라도 예외가 발생해야 함

    Category rootCategory = Category.newRootCategory(new CategoryId(CATEGORY_ID), new AccountId(OWNER_ID));

    assertThrows(CannotDeleteRootCategoryException.class,
        () -> rootCategory.delete(isRootDeleting));
  }

  private void initializeBookmarkRepository() {
    CategoryId categoryId = mock(CategoryId.class);
    when(categoryId.getId())
        .thenReturn(CATEGORY_ID);
    when(categoryRepository.nextId())
        .thenReturn(categoryId);
  }

  private Category generateMockCategory(String categoryId, int depth) {
    Category category = mock(Category.class);
    when(category.getId())
        .thenReturn(new CategoryId(categoryId));
    when(category.getDepth())
        .thenReturn(depth);
    return category;
  }
}
