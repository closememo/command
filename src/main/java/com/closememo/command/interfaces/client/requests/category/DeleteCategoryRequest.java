package com.closememo.command.interfaces.client.requests.category;

import javax.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class DeleteCategoryRequest {

  @NotBlank
  private String categoryId;
}
