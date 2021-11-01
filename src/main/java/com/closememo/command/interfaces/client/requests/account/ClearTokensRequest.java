package com.closememo.command.interfaces.client.requests.account;

import javax.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClearTokensRequest {

  @NotBlank
  private String accountId;
}
