package com.closememo.command.interfaces.client.requests.document;

import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.Getter;

@Getter
public class CreateDocumentRequest {

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
