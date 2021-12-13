package com.closememo.command.domain.category;

import com.closememo.command.domain.account.AccountId;
import java.util.Optional;

public interface CategoryRepository {

  CategoryId nextId();

  Category save(Category category);

  Optional<Category> findById(CategoryId categoryId);

  boolean existsByOwnerIdAndName(AccountId ownerId, String name);

  void delete(Category category);
}
