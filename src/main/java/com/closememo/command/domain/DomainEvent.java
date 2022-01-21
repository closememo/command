package com.closememo.command.domain;

import com.closememo.command.infra.messageing.Message;
import java.io.Serializable;
import java.time.ZonedDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DomainEvent extends Message implements Serializable {

  private static final long serialVersionUID = -846356380001543284L;

  private String aggregateId;
  private int eventVersion;
  private ZonedDateTime occurredOn;
  private boolean needAck;

  public DomainEvent(String aggregateId, int eventVersion) {
    this.aggregateId = aggregateId;
    this.eventVersion = eventVersion;
    this.occurredOn = ZonedDateTime.now();
    this.needAck = false;
  }

  public DomainEvent needAck() {
    this.needAck = true;
    return this;
  }

  @Override
  public MessageType getMessageType() {
    return MessageType.DOMAIN_EVENT;
  }
}
