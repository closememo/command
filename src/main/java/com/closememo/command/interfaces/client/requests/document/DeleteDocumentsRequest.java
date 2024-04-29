package com.closememo.command.interfaces.client.requests.document;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Getter;

@Getter
public class DeleteDocumentsRequest {

  @NotNull
  private List<@NotBlank String> documentIds;
}
