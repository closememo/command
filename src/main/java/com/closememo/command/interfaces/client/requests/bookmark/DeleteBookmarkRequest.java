package com.closememo.command.interfaces.client.requests.bookmark;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class DeleteBookmarkRequest {

  @NotBlank
  private String documentId;
}
