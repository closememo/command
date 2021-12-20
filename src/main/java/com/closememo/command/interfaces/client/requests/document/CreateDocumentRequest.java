package com.closememo.command.interfaces.client.requests.document;

import java.util.List;
import javax.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CreateDocumentRequest {

  @NotBlank
  private String categoryId;
  private String title;
  @NotBlank
  private String content;
  private List<String> tags;
  private DocumentOption option;

  @Getter
  public static class DocumentOption {
    private Boolean hasAutoTag;
  }
}
