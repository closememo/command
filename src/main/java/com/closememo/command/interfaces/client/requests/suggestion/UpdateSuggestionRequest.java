package com.closememo.command.interfaces.client.requests.suggestion;

import javax.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UpdateSuggestionRequest {

  @NotBlank
  private String suggestionId;
  @NotBlank
  private String content;
}
