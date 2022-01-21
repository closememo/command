package com.closememo.command.infra.messageing.handler;

import com.closememo.command.config.messaging.integration.AckFutureManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AckEventHandler {

  private final AckFutureManager ackFutureManager;

  public AckEventHandler(
      AckFutureManager ackFutureManager) {
    this.ackFutureManager = ackFutureManager;
  }

  @ServiceActivator(inputChannel = "AckEvent")
  public void handle(AckEvent payload) {
    log.debug("receiving ack event. aggregateId=" + payload.getAggregateId());
    ackFutureManager.wake(payload.getAggregateId());
  }
}
