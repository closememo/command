package com.closememo.command.application;

import com.closememo.command.domain.account.AccountId;
import com.closememo.command.infra.messageing.Message;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Command extends Message {

  protected CommandRequester requester;

  protected Command(CommandRequester requester) {
    Assert.notNull(requester, "'requester' must not be null.");
    this.requester = requester;
  }

  @Override
  public MessageType getMessageType() {
    return MessageType.COMMAND;
  }

  @JsonIgnore
  public boolean isSystemRequester() {
    return this.requester.isSystem();
  }

  @JsonIgnore
  public boolean isAdminRequester() {
    return this.requester.isAdmin();
  }

  @JsonIgnore
  public boolean isAccountRequester() {
    return this.requester.isAccount();
  }

  @JsonIgnore
  public boolean equalsAccountRequester(AccountId accountId) {
    return isAccountRequester() && this.requester.asAccount().equalsAccount(accountId);
  }

  @JsonIgnore
  public boolean isReliableRequester() {
    return isSystemRequester() || isAdminRequester();
  }
}
