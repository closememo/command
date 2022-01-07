package com.closememo.command.application.category;

import com.closememo.command.application.Command;
import com.closememo.command.application.Success;
import com.closememo.command.domain.AccessDeniedException;
import com.closememo.command.domain.account.AccountId;
import com.closememo.command.domain.category.Category;
import com.closememo.command.domain.category.CategoryId;
import com.closememo.command.domain.category.CategoryNotFoundException;
import com.closememo.command.domain.category.CategoryRepository;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CategoryCommandHandler {

  private final CategoryRepository categoryRepository;

  public CategoryCommandHandler(
      CategoryRepository categoryRepository) {
    this.categoryRepository = categoryRepository;
  }

  @Transactional
  @ServiceActivator(inputChannel = "CreateRootCategoryCommand")
  public CategoryId handle(CreateRootCategoryCommand command) {
    Category category = Category.newRootCategory(categoryRepository.nextId(), command.getOwnerId());
    Category savedCategory = categoryRepository.save(category);
    return savedCategory.getId();
  }

  @Transactional
  @ServiceActivator(inputChannel = "CreateCategoryCommand")
  public CategoryId handle(CreateCategoryCommand command) {
    Category parentCategory = categoryRepository.findById(command.getParentId())
        .orElseThrow(CategoryNotFoundException::new);

    Category category = Category.newOne(categoryRepository,
        command.getOwnerId(), command.getName(), parentCategory);
    checkAuthority(command, category.getOwnerId());

    Category savedCategory = categoryRepository.save(category);
    return savedCategory.getId();
  }

  @Transactional
  @ServiceActivator(inputChannel = "UpdateCategoryCommand")
  public CategoryId handle(UpdateCategoryCommand command) {
    Category category = categoryRepository.findById(command.getCategoryId())
        .orElseThrow(CategoryNotFoundException::new);
    checkAuthority(command, category.getOwnerId());

    category.update(categoryRepository, command.getName());
    Category savedCategory = categoryRepository.save(category);

    return savedCategory.getId();
  }

  @Transactional
  @ServiceActivator(inputChannel = "IncreaseCategoryCountCommand")
  public CategoryId handle(IncreaseCategoryCountCommand command) {
    Category category = categoryRepository.findById(command.getCategoryId())
        .orElseThrow(CategoryNotFoundException::new);
    checkAuthority(command, category.getOwnerId());

    category.increaseCount();
    Category savedCategory = categoryRepository.save(category);

    return savedCategory.getId();
  }

  @Transactional
  @ServiceActivator(inputChannel = "DecreaseCategoryCountCommand")
  public CategoryId handle(DecreaseCategoryCountCommand command) {
    Category category = categoryRepository.findById(command.getCategoryId())
        .orElseThrow(CategoryNotFoundException::new);
    checkAuthority(command, category.getOwnerId());

    category.decreaseCount();
    Category savedCategory = categoryRepository.save(category);

    return savedCategory.getId();
  }

  @Transactional
  @ServiceActivator(inputChannel = "DeleteCategoryCommand")
  public Success handle(DeleteCategoryCommand command) {
    Category category = categoryRepository.findById(command.getCategoryId())
        .orElseThrow(CategoryNotFoundException::new);
    checkAuthority(command, category.getOwnerId());

    category.delete(command.getIsRootDeleting());
    categoryRepository.delete(category);

    return Success.getInstance();
  }

  private static void checkAuthority(Command command, AccountId ownerId) {
    if (command.isReliableRequester()) {
      return;
    }

    if (!command.equalsAccountRequester(ownerId)) {
      throw new AccessDeniedException();
    }
  }
}
