package com.closememo.command.interfaces.system.requests.documents;

import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.Getter;

@Getter
public class UpdateAutoTagsRequest {

  @NotBlank
  private String documentId;
  private List<String> autoTags;
}
