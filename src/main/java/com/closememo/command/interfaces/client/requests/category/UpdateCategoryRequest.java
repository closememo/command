package com.closememo.command.interfaces.client.requests.category;

import javax.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UpdateCategoryRequest {

  @NotBlank
  private String categoryId;
  @NotBlank
  private String name;
}
