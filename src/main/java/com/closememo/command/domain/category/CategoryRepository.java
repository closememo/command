package com.closememo.command.domain.category;

import com.closememo.command.domain.account.AccountId;
import java.util.Optional;
import java.util.stream.Stream;

public interface CategoryRepository {

  CategoryId nextId();

  Category save(Category category);

  Optional<Category> findById(CategoryId categoryId);

  Optional<Category> findRootCategory(AccountId ownerId);

  Stream<Category> findAllByOwnerId(AccountId ownerId);

  long countByOwnerId(AccountId ownerId);

  boolean existsByOwnerIdAndName(AccountId ownerId, String name);

  void delete(Category category);
}
