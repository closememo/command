package com.closememo.command.application.category;

import com.closememo.command.application.Command;
import com.closememo.command.application.Success;
import com.closememo.command.domain.AccessDeniedException;
import com.closememo.command.domain.account.AccountId;
import com.closememo.command.domain.category.Category;
import com.closememo.command.domain.category.CategoryNotFoundException;
import com.closememo.command.domain.category.CategoryRepository;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BatchCategoryCommandHandler {

  private final CategoryRepository categoryRepository;

  public BatchCategoryCommandHandler(
      CategoryRepository categoryRepository) {
    this.categoryRepository = categoryRepository;
  }

  @Transactional
  @ServiceActivator(inputChannel = "BatchCategorySetCountCommand")
  public Success handle(BatchCategorySetCountCommand command) {
    Category category = categoryRepository.findById(command.getCategoryId())
        .orElseThrow(CategoryNotFoundException::new);
    checkAuthority(command, category.getOwnerId());

    category.batchSetCount(command.getCount());
    categoryRepository.save(category);

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
