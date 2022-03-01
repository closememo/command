package com.closememo.command.interfaces.client.requests.bookmark;

import javax.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class DeleteBookmarkRequest {

  @NotBlank
  private String documentId;
}
