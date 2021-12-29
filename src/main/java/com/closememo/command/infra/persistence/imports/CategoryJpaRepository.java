package com.closememo.command.infra.persistence.imports;

import com.closememo.command.domain.account.AccountId;
import com.closememo.command.domain.category.Category;
import com.closememo.command.domain.category.CategoryId;
import java.util.Optional;
import java.util.stream.Stream;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryJpaRepository extends JpaRepository<Category, CategoryId> {

  Optional<Category> findByOwnerIdAndIsRootTrue(AccountId ownerId);

  Stream<Category> findAllByOwnerId(AccountId ownerId);

  Stream<Category> findAllByParentId(CategoryId parentId);

  long countByOwnerId(AccountId ownerId);

  boolean existsByOwnerIdAndName(AccountId ownerId, String name);
}
