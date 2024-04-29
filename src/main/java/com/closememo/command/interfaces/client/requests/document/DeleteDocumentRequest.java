package com.closememo.command.interfaces.client.requests.document;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class DeleteDocumentRequest {

  @NotBlank
  private String documentId;
}
