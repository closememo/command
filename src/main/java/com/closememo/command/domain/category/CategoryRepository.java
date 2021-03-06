package com.closememo.command.domain.category;

import com.closememo.command.domain.account.AccountId;
import java.util.Optional;
import java.util.stream.Stream;

public interface CategoryRepository {

  CategoryId nextId();

  Category save(Category category);

  Optional<Category> findById(CategoryId categoryId);

  Optional<Category> findRootCategory(AccountId ownerId);

  Stream<Category> findAllByParentId(CategoryId categoryId);

  long countByOwnerId(AccountId ownerId);

  boolean existsByOwnerIdAndParentIdAndName(AccountId ownerId, CategoryId parentId, String name);

  void delete(Category category);
}
