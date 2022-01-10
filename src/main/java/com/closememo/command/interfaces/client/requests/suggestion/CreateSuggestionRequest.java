package com.closememo.command.interfaces.client.requests.suggestion;

import javax.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CreateSuggestionRequest {

  @NotBlank
  private String content;
}
