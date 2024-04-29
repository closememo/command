package com.closememo.command.interfaces.client.requests.suggestion;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class DeleteSuggestionRequest {

  @NotBlank
  private String suggestionId;
}
