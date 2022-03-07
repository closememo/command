package com.closememo.command.interfaces.system.requests.account;

import javax.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class DeleteAccountRequest {

  @NotBlank
  private String accountId;
}
