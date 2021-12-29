package com.closememo.command.interfaces.client.requests.document;

import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class DeleteDocumentsRequest {

  @NotNull
  private List<@NotBlank String> documentIds;
}
