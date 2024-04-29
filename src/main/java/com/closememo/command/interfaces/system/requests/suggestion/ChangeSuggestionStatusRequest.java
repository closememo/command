package com.closememo.command.interfaces.system.requests.suggestion;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ChangeSuggestionStatusRequest {

  @NotBlank
  private String suggestionId;
  @NotBlank
  private String status;
}
