package com.closememo.command.interfaces.client.requests.document;

import javax.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class DeleteDocumentRequest {

  @NotBlank
  private String documentId;
}
