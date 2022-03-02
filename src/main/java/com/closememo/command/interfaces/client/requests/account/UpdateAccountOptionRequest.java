package com.closememo.command.interfaces.client.requests.account;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UpdateAccountOptionRequest {

  private DocumentOrderType documentOrderType;
  private Integer documentCount;

  public enum DocumentOrderType {
    CREATED_NEWEST,
    CREATED_OLDEST,
    UPDATED_NEWEST,
  }
}
