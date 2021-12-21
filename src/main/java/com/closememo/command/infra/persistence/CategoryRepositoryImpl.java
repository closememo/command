package com.closememo.command.infra.persistence;

import com.closememo.command.domain.account.AccountId;
import com.closememo.command.domain.category.Category;
import com.closememo.command.domain.category.CategoryId;
import com.closememo.command.domain.category.CategoryRepository;
import com.closememo.command.infra.persistence.imports.CategoryJpaRepository;
import java.util.Optional;
import java.util.stream.Stream;
import org.springframework.stereotype.Repository;

@Repository
public class CategoryRepositoryImpl implements CategoryRepository {

  private final SequenceGenerator sequenceGenerator;
  private final CategoryJpaRepository categoryJpaRepository;

  public CategoryRepositoryImpl(
      SequenceGenerator sequenceGenerator,
      CategoryJpaRepository categoryJpaRepository) {
    this.sequenceGenerator = sequenceGenerator;
    this.categoryJpaRepository = categoryJpaRepository;
  }

  @Override
  public CategoryId nextId() {
    return new CategoryId(sequenceGenerator.generate());
  }

  @Override
  public Category save(Category category) {
    return categoryJpaRepository.save(category);
  }

  @Override
  public Optional<Category> findById(CategoryId categoryId) {
    return categoryJpaRepository.findById(categoryId);
  }

  @Override
  public Optional<Category> findRootCategory(AccountId ownerId) {
    return categoryJpaRepository.findByOwnerIdAndIsRootTrue(ownerId);
  }

  @Override
  public Stream<Category> findAllByOwnerId(AccountId ownerId) {
    return categoryJpaRepository.findAllByOwnerId(ownerId);
  }

  @Override
  public long countByOwnerId(AccountId ownerId) {
    return categoryJpaRepository.countByOwnerId(ownerId);
  }

  @Override
  public boolean existsByOwnerIdAndName(AccountId ownerId, String name) {
    return categoryJpaRepository.existsByOwnerIdAndName(ownerId, name);
  }

  @Override
  public void delete(Category category) {
    categoryJpaRepository.delete(category);
  }
}
