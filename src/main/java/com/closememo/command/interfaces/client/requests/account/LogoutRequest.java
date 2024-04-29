package com.closememo.command.interfaces.client.requests.account;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LogoutRequest {

  @NotBlank
  private String tokenId;
}
