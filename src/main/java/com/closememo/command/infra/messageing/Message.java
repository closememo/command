package com.closememo.command.infra.messageing;

public abstract class Message {

  public abstract MessageType getMessageType();

  public enum MessageType {
    COMMAND, DOMAIN_EVENT
  }
}
