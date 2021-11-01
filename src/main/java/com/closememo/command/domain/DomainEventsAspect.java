package com.closememo.command.domain;

import com.closememo.command.infra.messageing.publisher.MessagePublisher;
import java.util.Optional;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Aspect
@Component
public class DomainEventsAspect {

  private final MessagePublisher messagePublisher;

  public DomainEventsAspect(MessagePublisher messagePublisher) {
    this.messagePublisher = messagePublisher;
  }

  @Before("execution(* com.closememo.command.application..*(..))" +
      " && @annotation(org.springframework.transaction.annotation.Transactional)")
  public void before() {
    Optional<TransactionSynchronization> eventsTransactionSynchronization =
        TransactionSynchronizationManager.getSynchronizations().stream()
            .filter(transactionSynchronization ->
                transactionSynchronization instanceof EventsTransactionSynchronization)
            .findAny();

    if (eventsTransactionSynchronization.isEmpty()) {
      TransactionSynchronizationManager.registerSynchronization(
          new EventsTransactionSynchronization(messagePublisher));
    }
  }

  private static class EventsTransactionSynchronization implements TransactionSynchronization {

    private final MessagePublisher messagePublisher;

    public EventsTransactionSynchronization(MessagePublisher messagePublisher) {
      this.messagePublisher = messagePublisher;
    }

    @Override
    public void afterCommit() {
      Events.getDomainEvents().forEach(messagePublisher::publish);
    }

    @Override
    public void afterCompletion(int status) {
      Events.clear();
    }
  }
}
