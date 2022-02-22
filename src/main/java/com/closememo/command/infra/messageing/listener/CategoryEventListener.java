package com.closememo.command.infra.messageing.listener;

import com.closememo.command.application.SystemCommandRequester;
import com.closememo.command.application.category.CreateRootCategoryCommand;
import com.closememo.command.application.category.DecreaseCategoryCountCommand;
import com.closememo.command.application.category.DeleteCategoryCommand;
import com.closememo.command.application.category.IncreaseCategoryCountCommand;
import com.closememo.command.domain.account.AccountCreatedEvent;
import com.closememo.command.domain.account.AccountDeletedEvent;
import com.closememo.command.domain.category.Category;
import com.closememo.command.domain.category.CategoryDeletedEvent;
import com.closememo.command.domain.category.CategoryNotFoundException;
import com.closememo.command.domain.category.CategoryRepository;
import com.closememo.command.domain.document.DocumentCategoryUpdatedEvent;
import com.closememo.command.domain.document.DocumentCreatedEvent;
import com.closememo.command.domain.document.DocumentDeletedEvent;
import com.closememo.command.infra.messageing.publisher.MessagePublisher;
import java.util.stream.Stream;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CategoryEventListener {

  private final CategoryRepository categoryRepository;
  private final MessagePublisher messagePublisher;

  public CategoryEventListener(
      CategoryRepository categoryRepository,
      MessagePublisher messagePublisher) {
    this.categoryRepository = categoryRepository;
    this.messagePublisher = messagePublisher;
  }

  @ServiceActivator(inputChannel = "AccountCreatedEvent")
  @Transactional
  public void handle(AccountCreatedEvent payload) {
    CreateRootCategoryCommand command = new CreateRootCategoryCommand(
        SystemCommandRequester.getInstance(), payload.getAccountId());
    messagePublisher.publish(command);
  }

  @ServiceActivator(inputChannel = "AccountDeletedEvent")
  @Transactional
  public void handle(AccountDeletedEvent payload) {
    try (Stream<Category> categories = categoryRepository.findAllByOwnerId(payload.getAccountId())) {
      categories.forEach(category -> {
        DeleteCategoryCommand command = new DeleteCategoryCommand(
            SystemCommandRequester.getInstance(), category.getId(), category.getIsRoot());
        messagePublisher.publish(command);
      });
    }
  }

  @ServiceActivator(inputChannel = "DocumentCreatedEvent")
  @Transactional
  public void handle(DocumentCreatedEvent payload) {
    Category category = categoryRepository.findById(payload.getCategoryId())
        .orElseThrow(CategoryNotFoundException::new);
    IncreaseCategoryCountCommand command = new IncreaseCategoryCountCommand(
        SystemCommandRequester.getInstance(), category.getId());
    messagePublisher.publish(command);
  }

  @ServiceActivator(inputChannel = "DocumentCategoryUpdatedEvent")
  @Transactional
  public void handle(DocumentCategoryUpdatedEvent payload) {
    Category previousCategory = categoryRepository.findById(payload.getPreviousCategoryId())
        .orElseThrow(CategoryNotFoundException::new);
    Category category = categoryRepository.findById(payload.getCategoryId())
        .orElseThrow(CategoryNotFoundException::new);

    DecreaseCategoryCountCommand decreaseCategoryCountCommand = new DecreaseCategoryCountCommand(
        SystemCommandRequester.getInstance(), previousCategory.getId());
    IncreaseCategoryCountCommand increaseCategoryCountCommand = new IncreaseCategoryCountCommand(
        SystemCommandRequester.getInstance(), category.getId());

    messagePublisher.publish(decreaseCategoryCountCommand);
    messagePublisher.publish(increaseCategoryCountCommand);
  }

  @ServiceActivator(inputChannel = "DocumentDeletedEvent")
  @Transactional
  public void handle(DocumentDeletedEvent payload) {
    // 카테고리가 이미 삭제된 경우 무시한다.
    categoryRepository.findById(payload.getCategoryId()).ifPresent(category -> {
      DecreaseCategoryCountCommand command = new DecreaseCategoryCountCommand(
          SystemCommandRequester.getInstance(), category.getId());
      messagePublisher.publish(command);
    });
  }

  @ServiceActivator(inputChannel = "CategoryDeletedEvent")
  @Transactional
  public void handle(CategoryDeletedEvent payload) {
    try (Stream<Category> categories = categoryRepository.findAllByParentId(payload.getCategoryId())) {
      categories.forEach(category -> {
        DeleteCategoryCommand command = new DeleteCategoryCommand(
            SystemCommandRequester.getInstance(), category.getId(), category.getIsRoot());
        messagePublisher.publish(command);
      });
    }
  }
}
