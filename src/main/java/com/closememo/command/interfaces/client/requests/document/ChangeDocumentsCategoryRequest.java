package com.closememo.command.interfaces.client.requests.document;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Getter;

@Getter
public class ChangeDocumentsCategoryRequest {

  @NotNull
  private List<@NotBlank String> documentIds;
  @NotBlank
  private String categoryId;
}
