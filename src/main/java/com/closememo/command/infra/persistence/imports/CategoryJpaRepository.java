package com.closememo.command.infra.persistence.imports;

import com.closememo.command.domain.account.AccountId;
import com.closememo.command.domain.category.Category;
import com.closememo.command.domain.category.CategoryId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryJpaRepository extends JpaRepository<Category, CategoryId> {

  boolean existsByOwnerIdAndName(AccountId ownerId, String name);
}
