package com.closememo.command.interfaces.system.requests.documents;

import java.util.List;
import javax.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UpdateAutoTagsRequest {

  @NotBlank
  private String documentId;
  private List<String> autoTags;
}
