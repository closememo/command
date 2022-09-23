package com.closememo.command.infra.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.closememo.command.domain.account.AccountId;
import com.closememo.command.domain.category.Category;
import com.closememo.command.domain.category.CategoryId;
import com.closememo.command.domain.category.CategoryRepository;
import com.closememo.command.infra.persistence.imports.CategoryJpaRepository;
import com.closememo.command.test.ImportSequenceGenerator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ImportSequenceGenerator
@ExtendWith(SpringExtension.class)
@DataJpaTest
@ActiveProfiles("test")
class CategoryRepositoryImplTest {

  private static final String OWNER_ID = "ownerId";

  @Autowired
  private CategoryJpaRepository categoryJpaRepository;
  @Autowired
  private SequenceGenerator sequenceGenerator;
  private CategoryRepository categoryRepository;

  @BeforeEach
  public void beforeEach() {
    categoryRepository = new CategoryRepositoryImpl(sequenceGenerator, categoryJpaRepository);
  }

  @AfterEach
  public void afterEach() {
    categoryJpaRepository.deleteAll();
  }

  @Test
  @DisplayName("CategoryId 생성")
  public void createCategoryId() {
    CategoryId categoryId = categoryRepository.nextId();
    assertNotNull(categoryId);
  }

  @Test
  @DisplayName("Category 저장 후 findById")
  public void saveAndFindById() {
    AccountId accountId = new AccountId(OWNER_ID);
    CategoryId categoryId = categoryRepository.nextId();
    Category category = Category.newRootCategory(categoryId, accountId);
    // 저장
    categoryRepository.save(category);
    // findById
    Optional<Category> optionalCategory = categoryRepository.findById(categoryId);
    assertTrue(optionalCategory.isPresent());
    // 조회한 Category 값 확인
    Category saved = optionalCategory.get();
    assertEquals(categoryId, saved.getId());
    assertEquals(accountId, saved.getOwnerId());
    // 삭제 후 확인
    categoryRepository.delete(category);
    assertTrue(categoryRepository.findById(saved.getId()).isEmpty());
  }

  @Test
  @DisplayName("RootCategory 저장 후 findRootCategory")
  public void saveRootCategoryAndFindRootCategory() {
    AccountId accountId = new AccountId(OWNER_ID);
    // Root(Parent) Category 저장
    CategoryId rootCategoryId = categoryRepository.nextId();
    Category rootCategory = Category.newRootCategory(rootCategoryId, accountId);
    categoryRepository.save(rootCategory);
    // Child Category 저장
    Category childCategory = Category.newOne(categoryRepository,
        accountId, "새이름", rootCategory);
    categoryRepository.save(childCategory);
    // findRootCategory
    Optional<Category> optionalRoot = categoryRepository.findRootCategory(accountId);
    assertTrue(optionalRoot.isPresent());
    // 조회한 Root Category 검증
    Category savedRoot = optionalRoot.get();
    assertEquals(rootCategoryId, savedRoot.getId());
    assertTrue(savedRoot.getIsRoot());
    // 삭제
    categoryRepository.delete(rootCategory);
    categoryRepository.delete(childCategory);
  }

  @Test
  @DisplayName("다수의 Category 저장 후 부모 CategoryId 로 findAllByParentId")
  public void saveCategoriesAndFindAllByParentId() {
    AccountId accountId = new AccountId(OWNER_ID);
    // Root(Parent) Category 저장
    CategoryId rootCategoryId = categoryRepository.nextId();
    Category rootCategory = Category.newRootCategory(rootCategoryId, accountId);
    categoryRepository.save(rootCategory);
    // Child Category 1 저장
    Category childCategory1 = Category.newOne(categoryRepository,
        accountId, "새이름1", rootCategory);
    categoryRepository.save(childCategory1);
    // Child Category 2 저장
    Category childCategory2 = Category.newOne(categoryRepository,
        accountId, "새이름2", rootCategory);
    categoryRepository.save(childCategory2);
    // Root Category 를 Parent 로 하는 Category 목록 조회 (findAllByParentId)
    List<Category> children = categoryRepository.findAllByParentId(rootCategoryId)
        .collect(Collectors.toList());

    List<CategoryId> childrenCategoryIds = children.stream()
        .map(Category::getId)
        .collect(Collectors.toList());
    // 검증
    assertEquals(2, children.size());
    assertTrue(childrenCategoryIds.contains(childCategory1.getId()));
    assertTrue(childrenCategoryIds.contains(childCategory2.getId()));
    // 삭제
    categoryRepository.delete(rootCategory);
    categoryRepository.delete(childCategory1);
    categoryRepository.delete(childCategory2);
  }

  @Test
  @DisplayName("순차적으로 Category 저장하며 countByOwnerId")
  public void saveCategoriesAndCountByOwnerId() {
    AccountId accountId = new AccountId(OWNER_ID);
    // 저장 전 countByOwnerId
    assertEquals(0, categoryRepository.countByOwnerId(accountId));
    // Category 하나 저장 후 countByOwnerId
    Category rootCategory = Category.newRootCategory(categoryRepository.nextId(), accountId);
    categoryRepository.save(rootCategory);
    assertEquals(1, categoryRepository.countByOwnerId(accountId));
    // Category 하나 저장 후 countByOwnerId
    Category childCategory1 = Category.newOne(categoryRepository,
        accountId, "새이름1", rootCategory);
    categoryRepository.save(childCategory1);
    assertEquals(2, categoryRepository.countByOwnerId(accountId));
    // Category 하나 저장 후 countByOwnerId
    Category childCategory2 = Category.newOne(categoryRepository,
        accountId, "새이름2", rootCategory);
    categoryRepository.save(childCategory2);
    assertEquals(3, categoryRepository.countByOwnerId(accountId));
    // 삭제 후 countByOwnerId
    categoryRepository.delete(rootCategory);
    categoryRepository.delete(childCategory1);
    categoryRepository.delete(childCategory2);
    assertEquals(0, categoryRepository.countByOwnerId(accountId));
  }

  @Test
  @DisplayName("Category 이름 중복 검사 - existsByOwnerIdAndParentIdAndName")
  public void saveCategoryAndExistsByOwnerIdAndParentIdAndName() {
    AccountId accountId = new AccountId(OWNER_ID);
    // Root(Parent) Category 저장
    Category rootCategory = Category.newRootCategory(categoryRepository.nextId(), accountId);
    categoryRepository.save(rootCategory);
    // Parent Category 아래에 이름 중복 검사
    assertFalse(categoryRepository.existsByOwnerIdAndParentIdAndName(
        accountId, rootCategory.getId(), "새이름"));
    // Child Category 저장
    Category childCategory = Category.newOne(categoryRepository,
        accountId, "새이름", rootCategory);
    categoryRepository.save(childCategory);
    // Parent Category 아래에 이름 중복 검사
    assertTrue(categoryRepository.existsByOwnerIdAndParentIdAndName(
        accountId, rootCategory.getId(), "새이름"));
    // 삭제 후 Parent Category 아래에 이름 중복 검사
    categoryRepository.delete(rootCategory);
    categoryRepository.delete(childCategory);
    assertFalse(categoryRepository.existsByOwnerIdAndParentIdAndName(
        accountId, rootCategory.getId(), "새이름"));
  }
}
