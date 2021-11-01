package com.closememo.command.interfaces.client.requests.document;

import java.util.List;
import javax.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UpdateDocumentRequest {

  @NotBlank
  private String documentId;
  private String title;
  @NotBlank
  private String content;
  private List<String> tags;
}
