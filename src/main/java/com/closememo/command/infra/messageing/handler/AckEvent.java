package com.closememo.command.infra.messageing.handler;

import com.closememo.command.infra.messageing.Message;
import java.time.ZonedDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AckEvent extends Message {

  private String aggregateId;
  private int eventVersion;
  private ZonedDateTime occurredOn;

  @Override
  public MessageType getMessageType() {
    return MessageType.ACK_EVENT;
  }
}
