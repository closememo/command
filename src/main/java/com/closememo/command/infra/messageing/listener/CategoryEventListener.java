package com.closememo.command.infra.messageing.listener;

import com.closememo.command.application.SystemCommandRequester;
import com.closememo.command.application.category.CreateRootCategoryCommand;
import com.closememo.command.application.category.DeleteCategoryCommand;
import com.closememo.command.domain.account.AccountCreatedEvent;
import com.closememo.command.domain.account.AccountDeletedEvent;
import com.closememo.command.domain.category.Category;
import com.closememo.command.domain.category.CategoryRepository;
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
            SystemCommandRequester.getInstance(), category.getId());
        messagePublisher.publish(command);
      });
    }
  }
}
