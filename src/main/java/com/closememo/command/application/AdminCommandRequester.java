package com.closememo.command.application;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AdminCommandRequester extends CommandRequester {

  private String adminId;

  public AdminCommandRequester(String adminId) {
    super((CommandRequesterType.ADMIN));
    this.adminId = adminId;
  }
}
