package com.closememo.command.domain;

import com.closememo.command.config.messaging.integration.AckFutureManager;
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

  private final AckFutureManager ackFutureManager;
  private final MessagePublisher messagePublisher;

  public DomainEventsAspect(
      AckFutureManager ackFutureManager,
      MessagePublisher messagePublisher) {
    this.ackFutureManager = ackFutureManager;
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
          new EventsTransactionSynchronization(ackFutureManager, messagePublisher));
    }
  }

  private static class EventsTransactionSynchronization implements TransactionSynchronization {

    private final AckFutureManager ackFutureManager;
    private final MessagePublisher messagePublisher;

    public EventsTransactionSynchronization(
        AckFutureManager ackFutureManager,
        MessagePublisher messagePublisher) {
      this.ackFutureManager = ackFutureManager;
      this.messagePublisher = messagePublisher;
    }

    /**
     * DomainEvent.isNeedAck 가 true 이면 수신한 쪽에서 발신하는 ack 이벤트를 대기한다.
     * 이 때, 수신하는 쪽에서 문제가 생겨도 발신하는 쪽(이 컴포넌트)의 로직을 롤백하지는 않는다. (단순 대기)
     */
    @Override
    public void afterCommit() {
      for (DomainEvent domainEvent : Events.getDomainEvents()) {
        if (domainEvent.isNeedAck()) {
          ackFutureManager.submit(domainEvent.getAggregateId());
        }
        messagePublisher.publish(domainEvent);
        if (domainEvent.isNeedAck()) {
          ackFutureManager.wait(domainEvent.getAggregateId());
        }
      }
    }

    @Override
    public void afterCompletion(int status) {
      Events.clear();
    }
  }
}
