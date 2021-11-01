package com.closememo.command.application;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

@JsonSubTypes({
    @Type(value = AccountCommandRequester.class, name = "ACCOUNT"),
    @Type(value = AdminCommandRequester.class, name = "ADMIN"),
    @Type(value = AnonymousCommandRequester.class, name = "ANONYMOUS"),
    @Type(value = SystemCommandRequester.class, name = "SYSTEM"),
})
@JsonTypeInfo(use = Id.NAME, property = "type", visible = true)
@Getter
@NoArgsConstructor
public class CommandRequester {

  protected CommandRequesterType type;

  public CommandRequester(CommandRequesterType type) {
    this.type = type;
  }

  @JsonIgnore
  public boolean isAccount() {
    return this.type == CommandRequesterType.ACCOUNT;
  }

  @JsonIgnore
  public boolean isSystem() {
    return this.type == CommandRequesterType.SYSTEM;
  }

  @JsonIgnore
  public boolean isAdmin() {
    return this.type == CommandRequesterType.ADMIN;
  }

  @JsonIgnore
  public AccountCommandRequester asAccount() {
    Assert.isTrue(isAccount(), "Illegal requester type.");
    return ((AccountCommandRequester) this);
  }

  @JsonIgnore
  public AdminCommandRequester asAdmin() {
    Assert.isTrue(isAdmin(), "Illegal requester type.");
    return ((AdminCommandRequester) this);
  }

  @JsonIgnore
  public SystemCommandRequester asSystem() {
    Assert.isTrue(isSystem(), "Illegal requester type.");
    return ((SystemCommandRequester) this);
  }

  public enum CommandRequesterType {
    ANONYMOUS, ACCOUNT, SYSTEM, ADMIN
  }
}
