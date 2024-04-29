package com.closememo.command.interfaces.client.requests.bookmark;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CreateBookmarkRequest {

  @NotBlank
  private String documentId;
}
