package com.closememo.command.interfaces.client.requests.bookmark;

import javax.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CreateBookmarkRequest {

  @NotBlank
  private String documentId;
}
