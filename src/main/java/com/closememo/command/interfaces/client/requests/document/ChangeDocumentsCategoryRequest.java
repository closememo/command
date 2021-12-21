package com.closememo.command.interfaces.client.requests.document;

import java.util.List;
import javax.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ChangeDocumentsCategoryRequest {

  @NotBlank
  private List<String> documentIds;
  @NotBlank
  private String categoryId;
}
