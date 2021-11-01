package com.closememo.command.infra.messageing.publisher;

import com.closememo.command.domain.DomainEvent;
import com.closememo.command.application.Command;

public interface MessagePublisher {

  void publish(DomainEvent domainEvent);

  void publish(Command command);
}
